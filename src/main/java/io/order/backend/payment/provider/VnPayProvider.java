package io.order.backend.payment.provider;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.stereotype.Service;

import io.order.backend.common.exception.InternalException;
import io.order.backend.payment.config.VNPayConfig;
import io.order.backend.payment.dto.CallbackPaymentDTO;
import io.order.backend.payment.dto.PaymentDTO;
import io.order.backend.payment.dto.PaymentIntentDTO;
import io.order.backend.payment.dto.QueryPaymentResponseRawDTO;
import io.order.backend.payment.dto.RefundInfoDTO;
import io.order.backend.payment.dto.RefundResponseDTO;
import lombok.RequiredArgsConstructor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class VnPayProvider implements ProviderPayment {

    private final VNPayConfig config;
    private final ObjectMapper objectMapper;
    private final OkHttpClient okHttpClient;

    @Override
    public PaymentIntentDTO createPaymentUrl(PaymentDTO paymentDTO) throws Exception {
        String orderType = "other";
        BigDecimal amount = paymentDTO.getAmount().multiply(BigDecimal.valueOf(100));

        String vnp_TxnRef = config.getRandomNumber(8);

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", config.getVnp_Version());
        vnp_Params.put("vnp_Command", config.getVnp_Command());
        vnp_Params.put("vnp_TmnCode", config.getVnp_TmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", paymentDTO.getDescription() != null ? paymentDTO.getDescription() : "Thanh toan don hang:" + vnp_TxnRef);
        vnp_Params.put("vnp_OrderType", orderType);
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", config.getVnp_ReturnUrl());
        vnp_Params.put("vnp_IpAddr", "10.0.0.0");

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        boolean first = true;
        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && !fieldValue.isEmpty()) {

                if (!first) {
                    hashData.append("&");
                    query.append("&");
                }
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8));

                first = false;
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = config.hmacSHA512(config.getSecretKey(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = config.getVnp_PayUrl() + "?" + queryUrl;
        return PaymentIntentDTO.builder()
                .provider("VNPAY")
                .transactionId(vnp_TxnRef)
                .paymentUrl(paymentUrl)
                .build();
    }

    public CallbackPaymentDTO callback(Map<String, String> params) throws Exception {
        String vnpSecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();

        for (int i = 0; i < fieldNames.size(); i++) {
            String name = fieldNames.get(i);
            String value = params.get(name);

            if (value != null && !value.isEmpty()) {
                hashData.append(name)
                        .append("=")
                        .append(URLEncoder.encode(value, StandardCharsets.UTF_8));

                if (i < fieldNames.size() - 1) {
                    hashData.append("&");
                }
            }
        }
        String checkHash = config.hmacSHA512(config.getSecretKey(), hashData.toString());

        if (!checkHash.equals(vnpSecureHash)) {
            throw new InternalException("Invalid MAC");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String transactionId = params.get("vnp_TransactionNo");
        String vnpTxnRef = params.get("vnp_TxnRef");

        if ("00".equals(responseCode)) {
            return CallbackPaymentDTO.builder()
                    .responseCode(0)
                    .transactionId(vnpTxnRef)
                    .providerTransactionId(transactionId)
                    .build();
        }

        return CallbackPaymentDTO.builder()
                .responseCode(-1)
                .transactionId(vnpTxnRef)
                .providerTransactionId(transactionId)
                .build();
    }

    public RefundResponseDTO refund(RefundInfoDTO req) throws Exception {
        try {
            String requestId = UUID.randomUUID().toString().replace("-", "");

            String createDate = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new Date());

            long amount = req.getAmount() * 100;

            String type = "02";

            if (req.getType() == "FULL") {
                type = "02";
            } else if (req.getType() == "PARTIAL") {
                type = "03";
            }

            String data = String.join("|",
                    requestId,
                    config.getVnp_Version(),
                    "refund",
                    config.getVnp_TmnCode(),
                    type,
                    req.getTransactionId(),
                    String.valueOf(amount),
                    req.getProviderTransactionId() == null ? "" : req.getProviderTransactionId(),
                    req.getTransactionDate(),
                    req.getCreateBy(),
                    createDate,
                    req.getIpAddress(),
                    req.getOrderInfo());

            String secureHash = config.hmacSHA512(config.getSecretKey(), data);

            Map<String, Object> body = new LinkedHashMap<>();
            body.put("vnp_RequestId", requestId);
            body.put("vnp_Version", config.getVnp_Version());
            body.put("vnp_Command", "refund");
            body.put("vnp_TmnCode", config.getVnp_TmnCode());
            body.put("vnp_TransactionType", type);
            body.put("vnp_TxnRef", req.getTransactionId());
            body.put("vnp_Amount", amount);
            body.put("vnp_TransactionNo", req.getProviderTransactionId());
            body.put("vnp_TransactionDate", req.getTransactionDate());
            body.put("vnp_CreateBy", req.getCreateBy());
            body.put("vnp_CreateDate", createDate);
            body.put("vnp_IpAddr", req.getIpAddress());
            body.put("vnp_OrderInfo", req.getOrderInfo());
            body.put("vnp_SecureHash", secureHash);

            String json = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(config.getVnp_RefundUrl())
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {

                String resBody = response.body().string();
                Map<String, Object> result = objectMapper.readValue(resBody, Map.class);

                if (!"00".equals(result.get("vnp_ResponseCode"))) {
                    throw new InternalException("VNPay refund failed: " + result.get("vnp_Message"));
                }

                return RefundResponseDTO.builder()
                        .responseCode(0)
                        .message((String) result.get("vnp_Message"))
                        .refundId((String) result.get("vnp_ResponseId"))
                        .status((String) result.get("vnp_TransactionStatus"))
                        .rawData(result)
                        .build();
            }

        } catch (Exception e) {
            throw new InternalException("Refund failed" + e.getMessage());
        }
    }

    @Override
    public QueryPaymentResponseRawDTO queryPaymentResult(String transactionCode, String transactionDate) throws Exception {
        try {
            String vnp_RequestId = config.getRandomNumber(8);
            String vnp_Version = config.getVnp_Version();
            String vnp_Command = "querydr";
            String vnp_TmnCode = config.getVnp_TmnCode();
            String vnp_TxnRef = transactionCode;
            String vnp_OrderInfo = "Kiem tra ket qua GD OrderId:" + transactionCode;
            String vnp_TransDate = transactionDate;
            String vnp_IpAddr = "10.0.0.0";

            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());


            Map<String, Object> body = new LinkedHashMap<>();
            body.put("vnp_RequestId", vnp_RequestId);
            body.put("vnp_Version", vnp_Version);
            body.put("vnp_Command", vnp_Command);
            body.put("vnp_TmnCode", vnp_TmnCode);
            body.put("vnp_TxnRef", vnp_TxnRef);
            body.put("vnp_OrderInfo", vnp_OrderInfo);
            body.put("vnp_TransactionDate", vnp_TransDate);
            body.put("vnp_CreateDate", vnp_CreateDate);
            body.put("vnp_IpAddr", vnp_IpAddr);

            String hash_Data= String.join("|", vnp_RequestId, vnp_Version, vnp_Command, vnp_TmnCode, vnp_TxnRef, vnp_TransDate, vnp_CreateDate, vnp_IpAddr, vnp_OrderInfo);

            log.info("Query payment result - Data: {}", hash_Data);

            String vnp_SecureHash = config.hmacSHA512(config.getSecretKey(), hash_Data.toString());

            log.info("Query payment result - Secure Hash: {}", vnp_SecureHash);

            body.put("vnp_SecureHash", vnp_SecureHash);

            String json = objectMapper.writeValueAsString(body);

            Request request = new Request.Builder()
                    .url(config.getVnp_ApiUrl())
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            try (Response response = okHttpClient.newCall(request).execute()) {
                String resBody = response.body().string();
                Map<String, Object> result = objectMapper.readValue(resBody, Map.class);

                if ("00".equals(result.get("vnp_ResponseCode"))) {
                    throw new InternalException("VNPay query failed: " + result.get("vnp_Message"));
                }

                String transactionStatus = (String) result.get("vnp_TransactionStatus");
                Integer responseCode;

                switch (transactionStatus) {
                    case "00":
                        responseCode = 0;
                        break;
                    case "01":
                        responseCode = 1;
                        break;
                    default:
                        responseCode = -1;
                        break;
                }

                return QueryPaymentResponseRawDTO.builder()
                        .responseCode(responseCode)
                        .message((String) result.get("vnp_Message"))
                        .transactionId((String) result.get("vnp_TxnRef"))
                        .providerTransactionId(String.valueOf(result.get("vnp_TransactionNo")))
                        .amount(result.get("vnp_Amount") != null
                                ? Long.parseLong(String.valueOf(result.get("vnp_Amount"))) / 100
                                : 0)
                        .rawData(result)
                        .build();
            }
        } catch (Exception e) {
            throw new InternalException("Query payment failed: " + e.getMessage());
        }
    }
}
