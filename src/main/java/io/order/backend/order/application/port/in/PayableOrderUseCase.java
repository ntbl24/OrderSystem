package io.order.backend.order.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public interface PayableOrderUseCase {
    PayableOrder lockForPayment(String id, String buyerId);
    void lockForCallback(String id);

    record PayableOrder(String id, String buyerId, BigDecimal amount, String currency, Instant expiresAt) {
    }
}
