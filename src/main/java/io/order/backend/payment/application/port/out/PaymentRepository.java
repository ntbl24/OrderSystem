package io.order.backend.payment.application.port.out;

import io.order.backend.order.domain.OrderId;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.payment.domain.TransactionRef;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Optional<Payment> find(PaymentId id);

    Optional<Payment> byReference(TransactionRef reference);

    Payment lock(PaymentId id);

    void save(Payment payment);

    boolean orderSettled(OrderId orderId);

    List<PaymentId> due(Instant now, int limit);

    Work claim(PaymentId id, Instant now);

    boolean ownsLease(PaymentId id, String token);

    void complete(PaymentId id, String token, Instant nextAttempt, String outcome);

    record Work(Payment payment, String token, int attempts, boolean abandonedRefund) {
    }
}
