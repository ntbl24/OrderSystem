package io.order.backend.payment.application.port;

import java.math.BigDecimal;
import java.time.Instant;

public interface PayableOrderPort {
    PayableOrder lockForPayment(String orderId, String buyerId);

    void lockForCallback(String orderId);

    record PayableOrder(String orderId, String buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
