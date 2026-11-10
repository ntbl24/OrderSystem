package com.abug.order.application.port.in;

import java.math.BigDecimal;

import com.abug.order.domain.OrderId;
import com.abug.payment.domain.PaymentId;

public interface PaymentEventUseCase {
    void paymentSucceeded(PaymentEvent event);
    void paymentRefunded(PaymentEvent event);

    record PaymentEvent(OrderId orderId, PaymentId paymentId, BigDecimal amount, String currency) {
    }
}
