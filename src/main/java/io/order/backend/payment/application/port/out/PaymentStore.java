package io.order.backend.payment.application.port.out;

import io.order.backend.payment.domain.Payment;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentStore {
    Optional<Payment> find(String id);

    Optional<Payment> byReference(String reference);

    Payment lock(String id);

    void save(Payment payment);

    boolean orderSettled(String orderId);

    List<String> due(Instant now, int limit);

    Work claim(String id, Instant now);

    boolean ownsLease(String id, String token);

    void complete(String id, String token, Instant nextAttempt, String outcome);

    record Work(Payment payment, String token, int attempts, boolean abandonedRefund) {
    }
}
