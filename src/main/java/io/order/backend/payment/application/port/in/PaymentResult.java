package io.order.backend.payment.application.port.in;

import io.order.backend.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResult(String id, String orderId, BigDecimal amount, String currency,
        String status, String refundStatus, Instant expiresAt, String paymentUrl) {
    public static PaymentResult from(Payment p, String url) {
        return new PaymentResult(p.id(), p.orderId(), p.amount(), p.currency(), p.status().name(),
                p.refundStatus().name(), p.expiresAt(), url);
    }
}
