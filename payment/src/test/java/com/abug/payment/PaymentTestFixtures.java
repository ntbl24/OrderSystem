package com.abug.payment;

import java.math.BigDecimal;
import java.time.Instant;

import com.abug.payment.domain.Payment;
import com.abug.payment.domain.PaymentId;
import com.abug.payment.domain.TransactionRef;
import com.abug.order.domain.OrderId;
import com.abug.user.domain.UserId;

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
