package io.order.backend.payment;

import java.math.BigDecimal;
import java.time.Instant;

import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.payment.domain.TransactionRef;
import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;

final class PaymentTestFixtures {
    private PaymentTestFixtures() {
    }

    static Payment pending(BigDecimal amount) {
        var now = Instant.parse("2026-10-14T08:00:00Z");
        return Payment.reconstitute(PaymentId.of("payment"), OrderId.of("order"),
                UserId.of("00000000-0000-0000-0000-000000000001"),
                amount, "VND", TransactionRef.of("ref"), now,
                now.plusSeconds(900), Payment.Status.PENDING, null, Payment.RefundStatus.NONE,
                null, null, null, null);
    }
}
