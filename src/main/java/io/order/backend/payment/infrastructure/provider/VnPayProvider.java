package io.order.backend.payment.infrastructure.provider;

import io.order.backend.payment.application.port.out.PaymentGateway;
import io.order.backend.payment.application.port.in.PaymentNotification;
import io.order.backend.payment.infrastructure.config.VNPayConfig;
import io.order.backend.payment.domain.Payment;
import okhttp3.*;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Component
public class VnPayProvider implements PaymentGateway {
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            .withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
    private final VNPayConfig config;
    private final OkHttpClient http;
    private final JsonMapper json;
    private static final long MAX_RESPONSE_SIZE = 32 * 1024;

    public VnPayProvider(VNPayConfig config, OkHttpClient http, JsonMapper json) {
        this.config = config;
        this.http = http.newBuilder()
                .callTimeout(java.time.Duration.ofSeconds(30)).retryOnConnectionFailure(false)
                .build();
        this.json = json;
    }

    public String paymentUrl(Payment p, String ip) {
        Map<String, String> f = new TreeMap<>();
        f.put("vnp_Version", "2.1.0");
        f.put("vnp_Command", "pay");
        f.put("vnp_TmnCode", config.tmnCode());
        f.put("vnp_Amount", Long.toString(p.providerAmount()));
        f.put("vnp_CurrCode", p.getCurrency());
        f.put("vnp_TxnRef", p.getTransactionRef().toString());
        f.put("vnp_OrderInfo", "Payment " + p.getTransactionRef().value());
        f.put("vnp_OrderType", "other");
        f.put("vnp_Locale", "vn");
        f.put("vnp_ReturnUrl", config.returnUrl());
        f.put("vnp_IpAddr", ip);
        f.put("vnp_CreateDate", DATE.format(p.getCreatedAt()));
        f.put("vnp_ExpireDate", DATE.format(p.getExpiresAt()));
        String canonical = canonical(f);
        return config.payUrl() + "?" + canonical + "&vnp_SecureHash=" + sign(canonical);
    }

    public boolean validSignature(Map<String, String> p) {
        return equalSignature(p.get("vnp_SecureHash"), sign(canonical(p)));
    }

    public PaymentNotification decodeNotification(Map<String, String> fields) {
        if (!validSignature(fields))
            return new PaymentNotification(false, false, null, null, null, false, null);
        boolean success = "00".equals(fields.get("vnp_ResponseCode"))
                && "00".equals(fields.get("vnp_TransactionStatus"));
        BigDecimal amount = null;
        String rawAmount = fields.getOrDefault("vnp_Amount", "");
        try {
            long units = Long.parseLong(rawAmount);
            if (units > 0 && Long.toString(units).equals(rawAmount))
                amount = BigDecimal.valueOf(units, 2);
        } catch (NumberFormatException ignored) {
            // Malformed amounts are rejected as payment data, not signature failures.
        }
        boolean valid = config.tmnCode().equals(fields.get("vnp_TmnCode")) && amount != null
                && fields.getOrDefault("vnp_ResponseCode", "").matches("[0-9]{2}")
                && Set.of("00", "01", "02", "04", "05", "06", "07", "09")
                        .contains(fields.getOrDefault("vnp_TransactionStatus", ""))
                && (!success || fields.getOrDefault("vnp_TransactionNo", "").matches("[1-9][0-9]{0,31}"));
        return new PaymentNotification(true, valid, fields.get("vnp_TxnRef"), amount,
                fields.get("vnp_CurrCode"), success, fields.get("vnp_TransactionNo"));
    }

    public Observation query(Payment p) {
        var f = base("querydr", UUID.randomUUID().toString().replace("-", ""), p, Instant.now());
        f.put("vnp_OrderInfo", "Query " + p.getTransactionRef().toString());
        f.put("vnp_SecureHash", sign(join(f, "RequestId", "Version", "Command", "TmnCode", "TxnRef", "TransactionDate",
                "CreateDate", "IpAddr", "OrderInfo")));
        var r = post(f);
        if (!verifiedResponse(p, r, "querydr", true))
            return new Observation(Observation.State.UNKNOWN, null);
        String status = r.get("vnp_TransactionStatus"), type = r.get("vnp_TransactionType");
        if ("02".equals(type) && "00".equals(status))
            return new Observation(Observation.State.REFUNDED, r.get("vnp_TransactionNo"));
        if (!"01".equals(type))
            return new Observation(Observation.State.UNKNOWN, null);
        if ("00".equals(status) && r.getOrDefault("vnp_TransactionNo", "").matches("[1-9][0-9]{0,31}"))
            return new Observation(Observation.State.SUCCEEDED, r.get("vnp_TransactionNo"));
        if ("01".equals(status))
            return new Observation(Observation.State.PENDING, null);
        if (Set.of("02", "03", "07", "09").contains(status))
            return new Observation(Observation.State.FAILED, null);
        return new Observation(Observation.State.UNKNOWN, null);
    }

    public RefundOutcome refund(Payment p) {
        var f = base("refund", p.getRefundRequestId(), p, p.getRefundCreatedAt());
        f.put("vnp_TransactionType", "02");
        f.put("vnp_Amount", Long.toString(p.providerAmount()));
        f.put("vnp_TransactionNo", p.getProviderTransactionNo());
        f.put("vnp_CreateBy", p.getRefundActor());
        f.put("vnp_OrderInfo", p.getRefundReason());
        f.put("vnp_SecureHash", sign(join(f, "RequestId", "Version", "Command", "TmnCode", "TransactionType", "TxnRef",
                "Amount", "TransactionNo", "TransactionDate", "CreateBy", "CreateDate", "IpAddr", "OrderInfo")));
        var r = post(f);
        if (!verifiedResponse(p, r, "refund", false))
            return RefundOutcome.UNCERTAIN;
        // Acceptance is not settlement. Only a later verified query can confirm a full
        // refund.
        return "02".equals(r.get("vnp_TransactionType")) ? RefundOutcome.ACCEPTED : RefundOutcome.UNCERTAIN;
    }

    private Map<String, String> base(String command, String id, Payment p, Instant date) {
        var f = new LinkedHashMap<String, String>();
        f.put("vnp_RequestId", id);
        f.put("vnp_Version", "2.1.0");
        f.put("vnp_Command", command);
        f.put("vnp_TmnCode", config.tmnCode());
        f.put("vnp_TxnRef", p.getTransactionRef().toString());
        f.put("vnp_TransactionDate", DATE.format(p.getCreatedAt()));
        f.put("vnp_CreateDate", DATE.format(date));
        f.put("vnp_IpAddr", config.serverIp());
        return f;
    }

    private boolean verifiedResponse(Payment p, Map<String, String> r, String command, boolean query) {
        String data = join(r, "ResponseId", "Command", "ResponseCode", "Message", "TmnCode", "TxnRef", "Amount",
                "BankCode", "PayDate", "TransactionNo", "TransactionType", "TransactionStatus", "OrderInfo");
        if (query)
            data += "|" + r.getOrDefault("vnp_PromotionCode", "") + "|" + r.getOrDefault("vnp_PromotionAmount", "");
        return equalSignature(r.get("vnp_SecureHash"), sign(data)) && "00".equals(r.get("vnp_ResponseCode"))
                && command.equals(r.get("vnp_Command")) && config.tmnCode().equals(r.get("vnp_TmnCode"))
                && p.getTransactionRef().equals(r.get("vnp_TxnRef"))
                && Long.toString(p.providerAmount()).equals(r.get("vnp_Amount"));
    }

    private Map<String, String> post(Map<String, String> f) {
        try {
            var req = new Request.Builder().url(config.apiUrl())
                    .post(RequestBody.create(json.writeValueAsString(f), MediaType.get("application/json"))).build();
            try (var response = http.newCall(req).execute()) {
                if (!response.isSuccessful() || response.body() == null)
                    return Map.of();
                var source = response.body().source();
                var buffer = new okio.Buffer();
                while (buffer.size() <= MAX_RESPONSE_SIZE
                        && source.read(buffer, MAX_RESPONSE_SIZE + 1 - buffer.size()) != -1) {
                    if (buffer.size() > MAX_RESPONSE_SIZE) {
                        return Map.of();
                    }
                }
                if (buffer.size() > MAX_RESPONSE_SIZE) {
                    return Map.of();
                }
                String body = buffer.readUtf8();
                var node = json.readTree(body);
                if (node == null || !node.isObject()) {
                    return Map.of();
                }
                var result = new HashMap<String, String>();
                node.properties().forEach(e -> result.put(e.getKey(), e.getValue().asString()));
                return result;
            }
        } catch (Exception ignored) {
            return Map.of();
        }
    }

    private static String join(Map<String, String> f, String... keys) {
        return String.join("|", Arrays.stream(keys).map(k -> f.getOrDefault("vnp_" + k, "")).toList());
    }

    public static String canonical(Map<String, String> f) {
        return f.entrySet().stream()
                .filter(e -> e.getKey().startsWith("vnp_")
                        && !Set.of("vnp_SecureHash", "vnp_SecureHashType").contains(e.getKey()) && e.getValue() != null
                        && !e.getValue().isEmpty())
                .sorted(Map.Entry.comparingByKey()).map(e -> encode(e.getKey()) + "=" + encode(e.getValue()))
                .collect(java.util.stream.Collectors.joining("&"));
    }

    private static String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.US_ASCII);
    }

    public String sign(String data) {
        try {
            var mac = Mac.getInstance("HmacSHA512");
            mac.init(new SecretKeySpec(config.secretKey().getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
            return HexFormat.of().formatHex(mac.doFinal(data.getBytes(StandardCharsets.UTF_8)));
        } catch (java.security.GeneralSecurityException e) {
            throw new IllegalStateException("Unable to sign provider request", e);
        }
    }

    private static boolean equalSignature(String actual, String expected) {
        if (actual == null || !actual.matches("[a-fA-F0-9]{128}"))
            return false;
        return MessageDigest.isEqual(HexFormat.of().parseHex(actual), HexFormat.of().parseHex(expected));
    }
}
