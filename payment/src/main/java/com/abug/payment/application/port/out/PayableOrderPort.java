package com.abug.payment.application.port.out;

import java.math.BigDecimal;
import java.time.Instant;

import com.abug.order.domain.OrderId;
import com.abug.user.domain.UserId;

public interface PayableOrderPort {
    PayableOrder lockForPayment(OrderId orderId, UserId buyerId);

    void lockForCallback(OrderId orderId);

    record PayableOrder(OrderId orderId, UserId buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
