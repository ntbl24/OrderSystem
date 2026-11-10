package io.order.backend.payment.application.port.in;

import io.order.backend.payment.domain.PaymentId;

public interface RefundPaymentUseCase {
    PaymentResult refund(PaymentId id, String key, String reason);
}
