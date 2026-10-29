package io.order.backend.order.application.port.in;

import java.math.BigDecimal;

import io.order.backend.order.domain.OrderId;
import io.order.backend.payment.domain.PaymentId;

public interface PaymentEventUseCase {
    void paymentSucceeded(PaymentEvent event);
    void paymentRefunded(PaymentEvent event);

    record PaymentEvent(OrderId orderId, PaymentId paymentId, BigDecimal amount, String currency) {
    }
}
