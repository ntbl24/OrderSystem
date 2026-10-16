package io.order.backend.payment;
import io.order.backend.payment.config.VNPayConfig;
import io.order.backend.payment.infrastructure.provider.VnPayProvider;
import okhttp3.OkHttpClient;
import tools.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.*;
import static org.assertj.core.api.Assertions.*;
class VnPayProviderTest {
    private final VnPayProvider provider=new VnPayProvider(new VNPayConfig("merchant","01234567890123456789012345678901","https://sandbox.vnpayment.vn/paymentv2/vpcpay.html","https://sandbox.vnpayment.vn/merchant_webapi/api/transaction","https://example.com/return","127.0.0.1"),new OkHttpClient(),JsonMapper.builder().build());
    @Test void signature_tamperedFields_rejectedWithoutMutatingInput() {
        var f=observation(); f.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(f)));
        var copy=Map.copyOf(f); assertThat(provider.validSignature(f)).isTrue(); assertThat(f).isEqualTo(copy);
        f.put("vnp_Amount","999"); assertThat(provider.validSignature(f)).isFalse();
        assertThat(provider.validSignature(Map.of())).isFalse();
    }
    @Test void observation_wrongMerchantAmountCurrencyReference_rejected() {
        var f=observation();
        f.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(f)));
        assertThat(provider.decodeNotification(f).dataValid()).isTrue();
        for(String field:List.of("vnp_TmnCode","vnp_Amount","vnp_TransactionNo","vnp_TransactionStatus")) {
            var tampered=new HashMap<>(f); tampered.put(field,"wrong"); tampered.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(tampered))); assertThat(provider.decodeNotification(tampered).dataValid()).isFalse();
        }
    }
    @Test void notification_signedFields_extractNeutralValuesAndPreserveSignatureRules() {
        var f=observation(); f.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(f)).toUpperCase(Locale.ROOT));
        var notification=provider.decodeNotification(f);
        assertThat(notification.signatureValid()).isTrue();
        assertThat(notification.dataValid()).isTrue();
        assertThat(notification.transactionReference()).isEqualTo("ref");
        assertThat(notification.amount()).isEqualByComparingTo(BigDecimal.TEN);
        assertThat(notification.currency()).isEqualTo("VND");
        assertThat(notification.successful()).isTrue();
        assertThat(notification.providerTransactionNo()).isEqualTo("123");
        f.put("vnp_Amount","999");
        assertThat(provider.decodeNotification(f).signatureValid()).isFalse();
    }
    @Test void notification_nonCanonicalOrOverflowAmount_rejectedAfterValidSignature() {
        for(String amount:List.of("01000","+1000","1000.0","-1000","0","9223372036854775808")) {
            var f=observation(); f.put("vnp_Amount",amount); f.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(f)));
            assertThat(provider.decodeNotification(f).signatureValid()).isTrue();
            assertThat(provider.decodeNotification(f).dataValid()).isFalse();
        }
    }
    @Test void notification_failedPayment_doesNotRequireSuccessfulTransactionNumber() {
        var f=observation(); f.put("vnp_ResponseCode","24"); f.put("vnp_TransactionStatus","02"); f.remove("vnp_TransactionNo");
        f.put("vnp_SecureHash",provider.sign(VnPayProvider.canonical(f)));
        assertThat(provider.decodeNotification(f).dataValid()).isTrue();
        assertThat(provider.decodeNotification(f).successful()).isFalse();
    }
    @Test void intent_localSigning_usesPersistedVietnamTimestampAndExactAmount() {
        String url=provider.paymentUrl(PaymentDomainTest.pending(BigDecimal.TEN),"127.0.0.1");
        assertThat(url).contains("vnp_CreateDate=20261014150000","vnp_ExpireDate=20261014151500","vnp_Amount=1000","vnp_CurrCode=VND");
    }
    private static HashMap<String,String> observation() {
        return new HashMap<>(Map.of("vnp_TmnCode","merchant","vnp_TxnRef","ref","vnp_Amount","1000","vnp_CurrCode","VND","vnp_ResponseCode","00","vnp_TransactionStatus","00","vnp_TransactionNo","123"));
    }
}
