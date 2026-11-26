package com.abug.order.application.port.in;

import java.math.BigDecimal;

import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.valueobject.PaymentId;

public interface PaymentEventUseCase {
    void paymentSucceeded(PaymentEvent event);
    void paymentRefunded(PaymentEvent event);

    record PaymentEvent(OrderId orderId, PaymentId paymentId, BigDecimal amount, String currency) {
    }
}
