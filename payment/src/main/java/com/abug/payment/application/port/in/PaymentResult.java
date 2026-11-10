package com.abug.payment.application.port.in;

import com.abug.payment.domain.Payment;
import java.math.BigDecimal;
import java.time.Instant;

public record PaymentResult(String id, String orderId, BigDecimal amount, String currency,
        String status, String refundStatus, Instant expiresAt, String paymentUrl) {
    public static PaymentResult from(Payment p, String url) {
        return new PaymentResult(p.getId().toString(), p.getOrderId().toString(), p.getAmount(), p.getCurrency(), p.getStatus().name(),
                p.getRefundStatus().name(), p.getExpiresAt(), url);
    }
}
