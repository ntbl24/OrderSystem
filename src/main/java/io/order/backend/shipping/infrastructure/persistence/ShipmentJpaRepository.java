package io.order.backend.shipping.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.*;

public interface ShipmentJpaRepository extends JpaRepository<ShipmentEntity, UUID> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ShipmentEntity e where e.id = :id")
    Optional<ShipmentEntity> lockById(@Param("id") UUID id);

    Optional<ShipmentEntity> findByOrderId(UUID orderId);

    Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber);
}
