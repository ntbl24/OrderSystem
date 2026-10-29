package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.payment.domain.Payment;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;

class PaymentLeaseTest {
    @Test
    void lease_excludesConcurrentClaimsAndIgnoresStaleCompletion() {
        var now = Instant.parse("2026-10-14T08:00:00Z");
        var payment = Payment.create("order", "buyer", BigDecimal.TEN, "VND", now, now.plusSeconds(900));
        var entity = PaymentPersistenceMapper.create(payment);
        assertThat(entity.claimReconciliation(now)).isNull();
        var first = entity.claimReconciliation(now.plusSeconds(60));
        assertThat(entity.claimReconciliation(now.plusSeconds(61))).isNull();
        entity.completeReconciliation("stale", now, "STALE");
        assertThat(entity.ownsLease(first.token())).isTrue();
        entity.completeReconciliation(first.token(), now.plusSeconds(300), "PENDING");
        assertThat(entity.ownsLease(first.token())).isFalse();
        assertThat(entity.claimReconciliation(now.plusSeconds(299))).isNull();
        assertThat(entity.claimReconciliation(now.plusSeconds(300)).attempts()).isEqualTo(2);
        assertThat(PaymentPersistenceMapper.domain(entity).getStatus()).isEqualTo(Payment.Status.PENDING);
    }
}
