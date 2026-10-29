package io.order.backend.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.*;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from OrderEntity e where e.id = :id")
    Optional<OrderEntity> lockById(@Param("id") UUID id);

    List<OrderEntity> findTop100ByUserIdOrderByCreatedAtDesc(UUID userId);

    Optional<OrderEntity> findByOrderNumber(String number);

    List<OrderEntity> findTop100ByOrderStatus(String status);

    List<OrderEntity> findTop100ByOrderStatusAndExpiresAtLessThanEqualOrderById(String status, java.time.Instant now);
}
