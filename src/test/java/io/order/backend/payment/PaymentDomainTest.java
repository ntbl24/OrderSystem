package io.order.backend.payment;
import io.order.backend.payment.domain.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;
class PaymentDomainTest {
    static Payment pending(BigDecimal amount) { var now=Instant.parse("2026-10-14T08:00:00Z"); return new Payment("payment","order","buyer",amount,"VND","ref",now,now.plusSeconds(900),Payment.Status.PENDING,null,Payment.RefundStatus.NONE,null,null,null,null); }
    @Test void amount_fractionalOrOverflow_rejected() {
        assertThatThrownBy(()->pending(new BigDecimal("1.1"))).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(()->pending(new BigDecimal("92233720368547759"))).isInstanceOf(ArithmeticException.class);
        assertThat(pending(new BigDecimal("123456789")).providerAmount()).isEqualTo(12345678900L);
    }
    @Test void observation_lateSuccessAfterFailure_preserved() {
        var p=pending(BigDecimal.TEN).observe(false,null).observe(true,"123");
        assertThat(p.status()).isEqualTo(Payment.Status.SUCCEEDED);
        assertThat(p.observe(false,null)).isSameAs(p);
        assertThatThrownBy(()->p.observe(true,"456")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void refund_acceptanceNotSettlement_requiresVerifiedReconciliation() {
        var p=pending(BigDecimal.TEN).observe(true,"123").requestRefund("request","admin","reason",Instant.now());
        assertThatThrownBy(()->p.refundState(Payment.RefundStatus.REFUNDED)).isInstanceOf(IllegalStateException.class);
        var accepted=p.refundState(Payment.RefundStatus.DISPATCHING).refundState(Payment.RefundStatus.ACCEPTED);
        assertThat(accepted.refundStatus()).isNotEqualTo(Payment.RefundStatus.REFUNDED);
        assertThat(accepted.refundState(Payment.RefundStatus.REFUNDED).refundStatus()).isEqualTo(Payment.RefundStatus.REFUNDED);
        assertThat(accepted.requestRefund("different","admin","reason",Instant.now()).refundRequestId()).isEqualTo("request");
    }
}
