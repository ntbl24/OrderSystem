package io.order.backend.payment.infrastructure.persistence;

import io.order.backend.order.domain.OrderId;
import io.order.backend.payment.application.port.out.PaymentRepository;
import io.order.backend.payment.domain.Payment;
import io.order.backend.payment.domain.PaymentId;
import io.order.backend.payment.domain.TransactionRef;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional(propagation = Propagation.MANDATORY)
public class JpaPaymentAdapter implements PaymentRepository {
    private final JpaPaymentRepository repository;

    @Override
    public Optional<Payment> find(PaymentId id) {
        return repository.findById(id.value()).map(PaymentPersistenceMapper::domain);
    }

    @Override
    public Optional<Payment> byReference(TransactionRef ref) {
        return repository.findByTransactionRef(ref.toString()).map(PaymentPersistenceMapper::domain);
    }

    @Override
    public Payment lock(PaymentId id) {
        return repository.findFreshForUpdate(id.value()).map(PaymentPersistenceMapper::domain)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));
    }

    @Override
    public void save(Payment payment) {
        var entity = repository.findById(payment.getId().value())
                .orElseGet(() -> PaymentPersistenceMapper.create(payment));
        PaymentPersistenceMapper.copy(payment, entity);
        repository.saveAndFlush(entity);
    }

    @Override
    public boolean orderSettled(OrderId id) {
        return repository.existsByOrderIdAndStatus(id.toString(), Payment.Status.SUCCEEDED);
    }

    @Override
    public List<PaymentId> due(Instant now, int limit) {
        if (limit == 0)
            return List.of();
        return repository.findDue(now, Payment.Status.PENDING,
                List.of(Payment.RefundStatus.REQUESTED, Payment.RefundStatus.DISPATCHING,
                        Payment.RefundStatus.ACCEPTED, Payment.RefundStatus.UNCERTAIN), Limit.of(limit))
                .stream().map(PaymentId::of).toList();
    }

    @Override
    public Work claim(PaymentId id, Instant now) {
        var entity = repository.findFreshForUpdate(id.value()).orElse(null);
        if (entity == null)
            return null;
        var lease = entity.claimReconciliation(now);
        return lease == null ? null : new Work(PaymentPersistenceMapper.domain(entity), lease.token(),
                lease.attempts(), entity.getRefundStatus() == Payment.RefundStatus.DISPATCHING);
    }

    @Override
    public boolean ownsLease(PaymentId id, String token) {
        return repository.findById(id.value()).map(entity -> entity.ownsLease(token)).orElse(false);
    }

    @Override
    public void complete(PaymentId id, String token, Instant next, String outcome) {
        repository.findFreshForUpdate(id.value()).ifPresent(entity -> entity.completeReconciliation(token, next, outcome));
    }
}
