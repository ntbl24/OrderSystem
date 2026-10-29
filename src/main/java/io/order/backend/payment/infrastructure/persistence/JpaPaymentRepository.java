package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.payment.domain.Payment;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface JpaPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID>, PaymentLockRepository {
    Optional<PaymentJpaEntity> findByTransactionRef(String transactionRef);

    boolean existsByOrderIdAndStatus(String orderId, Payment.Status status);

    @Query("""
            select p.id from PaymentJpaEntity p
            where p.nextAttempt <= :now
              and (p.leaseUntil is null or p.leaseUntil < :now)
              and (p.attempts < 8 or p.leaseToken is not null)
              and (p.status = :pending or p.refundStatus in :refunds)
            order by p.nextAttempt, p.id
            """)
    List<UUID> findDue(@Param("now") Instant now, @Param("pending") Payment.Status pending,
            @Param("refunds") Collection<Payment.RefundStatus> refunds, Limit limit);
}
