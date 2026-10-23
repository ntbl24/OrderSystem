package io.order.backend.payment.infrastructure.web.dto;

import io.order.backend.payment.application.port.in.PaymentResult;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResponse(String id, String orderId, BigDecimal amount, String currency,
        String status, String refundStatus, Instant expiresAt, String paymentUrl) {
    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(result.id(), result.orderId(), result.amount(), result.currency(), result.status(),
                result.refundStatus(), result.expiresAt(), result.paymentUrl());
    }
}
