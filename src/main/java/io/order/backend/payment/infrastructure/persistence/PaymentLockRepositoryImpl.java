package io.order.backend.payment.infrastructure.persistence;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PaymentLockRepositoryImpl implements PaymentLockRepository {
    private final EntityManager entityManager;

    @Override
    public Optional<PaymentJpaEntity> findFreshForUpdate(UUID id) {
        var entity = entityManager.find(PaymentJpaEntity.class, id);
        if (entity != null) {
            // Refresh even managed entities: a lookup before the order lock can cache stale state.
            entityManager.refresh(entity, LockModeType.PESSIMISTIC_WRITE);
        }
        return Optional.ofNullable(entity);
    }
}
