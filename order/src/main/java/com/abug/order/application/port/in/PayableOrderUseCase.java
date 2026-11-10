package com.abug.order.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

import com.abug.order.domain.OrderId;
import com.abug.user.domain.UserId;

public interface PayableOrderUseCase {
    PayableOrder lockForPayment(OrderId id, UserId buyerId);
    void lockForCallback(OrderId id);

    record PayableOrder(OrderId id, UserId buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
