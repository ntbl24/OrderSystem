package io.order.backend.order.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

import io.order.backend.order.domain.OrderId;
import io.order.backend.user.domain.UserId;

public interface PayableOrderUseCase {
    PayableOrder lockForPayment(OrderId id, UserId buyerId);
    void lockForCallback(OrderId id);

    record PayableOrder(OrderId id, UserId buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
