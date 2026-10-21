package io.order.backend.cart.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.repository.query.Param;

public interface CartJpaRepository extends JpaRepository<CartEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from CartEntity e where e.id = :id")
    Optional<CartEntity> lockById(@Param("id") UUID id);

    Optional<CartEntity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
