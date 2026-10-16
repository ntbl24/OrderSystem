package io.order.backend.payment.dto;

import io.order.backend.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(String id, String orderId, BigDecimal amount, String currency,
        String status, String refundStatus, Instant expiresAt, String paymentUrl) {
    public static PaymentResponse from(Payment p, String url) {
        return new PaymentResponse(p.id(), p.orderId(), p.amount(), p.currency(), p.status().name(),
                p.refundStatus().name(), p.expiresAt(), url);
    }
}
