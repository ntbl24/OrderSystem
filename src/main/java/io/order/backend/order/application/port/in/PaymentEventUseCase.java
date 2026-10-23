package io.order.backend.order.application.port.in;

import java.math.BigDecimal;

public interface PaymentEventUseCase {
    void paymentSucceeded(PaymentEvent event);
    void paymentRefunded(PaymentEvent event);

    record PaymentEvent(String orderId, String paymentId, BigDecimal amount, String currency) {
    }
}
