package io.order.backend.payment;
import io.order.backend.payment.domain.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.*;
class PaymentDomainTest {
    static Payment pending(BigDecimal amount) { var now=Instant.parse("2026-10-14T08:00:00Z"); return Payment.reconstitute("payment","order","buyer",amount,"VND","ref",now,now.plusSeconds(900),Payment.Status.PENDING,null,Payment.RefundStatus.NONE,null,null,null,null); }
    @Test void start_capsAttemptAtOrderExpiryOrFifteenMinutes() {
        var now = Instant.parse("2026-10-14T08:00:00Z");
        var early = Payment.create("order", "buyer", BigDecimal.TEN, "VND", now, now.plusSeconds(30));
        var capped = Payment.create("order", "buyer", BigDecimal.TEN, "VND", now, now.plusSeconds(1800));
        assertThat(early.getExpiresAt()).isEqualTo(now.plusSeconds(30));
        assertThat(capped.getExpiresAt()).isEqualTo(now.plusSeconds(900));
        assertThat(capped.getStatus()).isEqualTo(Payment.Status.PENDING);
        assertThat(capped.getRefundStatus()).isEqualTo(Payment.RefundStatus.NONE);
        assertThat(capped.getTransactionRef()).hasSize(32);
        assertThatThrownBy(() -> Payment.create("order", "buyer", BigDecimal.TEN, "VND", now, now))
                .isInstanceOf(IllegalStateException.class).hasMessage("Order expired");
    }
    @Test void refund_invalidReason_cannotBypassDomainRules() {
        var payment = pending(BigDecimal.TEN).observe(true, "123");
        for (String reason : new String[] {"", "invalid|reason", "invalid\nreason", "invalid\rreason", "x".repeat(256)})
            assertThatThrownBy(() -> payment.requestRefund("request", "admin", reason, Instant.now()))
                    .isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid refund reason");
    }
    @Test void exhaustedDispatch_requiresManualReviewRatherThanAnotherRefund() {
        var payment = pending(BigDecimal.TEN).observe(true, "123")
                .requestRefund("request", "admin", "reason", Instant.now())
                .refundState(Payment.RefundStatus.DISPATCHING);
        assertThat(payment.exhaustReconciliation().getRefundStatus()).isEqualTo(Payment.RefundStatus.MANUAL_REVIEW);
        assertThat(payment.exhaustReconciliation().requiresReconciliation()).isFalse();
    }
    @Test void transitions_mutateTheExistingAggregateAndRejectConflictingSettlement() {
        var payment = pending(BigDecimal.TEN);
        assertThat(payment.observe(false, null)).isSameAs(payment);
        assertThat(payment.getStatus()).isEqualTo(Payment.Status.FAILED);
        payment.observe(true, "123");
        assertThat(payment.getStatus()).isEqualTo(Payment.Status.SUCCEEDED);
        assertThatThrownBy(() -> payment.observe(true, "456")).isInstanceOf(IllegalArgumentException.class);
        assertThat(payment.getProviderTransactionNo()).isEqualTo("123");
        assertThat(payment.requestRefund("stable", "admin", "reason", Instant.now())).isSameAs(payment);
        assertThat(payment.getRefundStatus()).isEqualTo(Payment.RefundStatus.REQUESTED);
    }
    @Test void amount_fractionalOrOverflow_rejected() {
        assertThatThrownBy(()->pending(new BigDecimal("1.1"))).isInstanceOf(ArithmeticException.class);
        assertThatThrownBy(()->pending(new BigDecimal("92233720368547759"))).isInstanceOf(ArithmeticException.class);
        assertThat(pending(new BigDecimal("123456789")).providerAmount()).isEqualTo(12345678900L);
    }
    @Test void observation_lateSuccessAfterFailure_preserved() {
        var p=pending(BigDecimal.TEN).observe(false,null).observe(true,"123");
        assertThat(p.getStatus()).isEqualTo(Payment.Status.SUCCEEDED);
        assertThat(p.observe(false,null)).isSameAs(p);
        assertThatThrownBy(()->p.observe(true,"456")).isInstanceOf(IllegalArgumentException.class);
    }
    @Test void refund_acceptanceNotSettlement_requiresVerifiedReconciliation() {
        var p=pending(BigDecimal.TEN).observe(true,"123").requestRefund("request","admin","reason",Instant.now());
        assertThatThrownBy(()->p.refundState(Payment.RefundStatus.REFUNDED)).isInstanceOf(IllegalStateException.class);
        var accepted=p.refundState(Payment.RefundStatus.DISPATCHING).refundState(Payment.RefundStatus.ACCEPTED);
        assertThat(accepted.getRefundStatus()).isNotEqualTo(Payment.RefundStatus.REFUNDED);
        assertThat(accepted.refundState(Payment.RefundStatus.REFUNDED).getRefundStatus()).isEqualTo(Payment.RefundStatus.REFUNDED);
        assertThat(accepted.requestRefund("different","admin","reason",Instant.now()).getRefundRequestId()).isEqualTo("request");
    }
}
