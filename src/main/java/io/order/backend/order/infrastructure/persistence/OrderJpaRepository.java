package io.order.backend.order.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from OrderEntity e where e.id = :id")
    Optional<OrderEntity> lockById(@org.springframework.data.repository.query.Param("id") String id);

    List<OrderEntity> findTop100ByUserIdOrderByCreatedAtDesc(String userId);

    Optional<OrderEntity> findByOrderNumber(String number);

    List<OrderEntity> findTop100ByOrderStatus(String status);

    List<OrderEntity> findTop100ByOrderStatusAndExpiresAtLessThanEqualOrderById(String status, java.time.Instant now);
}
