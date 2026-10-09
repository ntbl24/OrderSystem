package io.order.backend.payment.infrastructure;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import io.order.backend.payment.domain.Payment;

@Repository
public interface MongoPaymentRepository extends MongoRepository<Payment, String>, PaymentRepository {
    Optional<Payment> findByOrderId(String orderId);
    Optional<Payment> findByPaymentIntentId(String paymentIntentId);
}
