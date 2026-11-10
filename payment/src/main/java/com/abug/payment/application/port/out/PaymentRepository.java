package com.abug.payment.application.port.out;

import com.abug.order.domain.OrderId;
import com.abug.payment.domain.Payment;
import com.abug.payment.domain.PaymentId;
import com.abug.payment.domain.TransactionRef;

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
