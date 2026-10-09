package io.order.backend.payment.infrastructure;

import java.util.Optional;

import io.order.backend.payment.domain.Payment;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(String id);
    boolean existsById(String id);
    void deleteById(String id);
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
}