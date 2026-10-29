package io.order.backend.payment.infrastructure.persistence;

import java.util.Optional;
import java.util.UUID;

public interface PaymentLockRepository {
    Optional<PaymentJpaEntity> findFreshForUpdate(UUID id);
}
