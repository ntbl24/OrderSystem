package io.order.backend.payment.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;

import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;

public interface PayableOrderPort {
    PayableOrder lockForPayment(OrderId orderId, UserId buyerId);

    void lockForCallback(OrderId orderId);

    record PayableOrder(OrderId orderId, UserId buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
