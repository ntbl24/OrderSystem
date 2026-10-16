package io.order.backend.shipping.infrastructure.persistence;

import org.springframework.data.jpa.repository.*;
import jakarta.persistence.LockModeType;
import java.util.*;

public interface ShipmentJpaRepository extends JpaRepository<ShipmentEntity, String> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from ShipmentEntity e where e.id = :id")
    Optional<ShipmentEntity> lockById(@org.springframework.data.repository.query.Param("id") String id);

    Optional<ShipmentEntity> findByOrderId(String orderId);

    Optional<ShipmentEntity> findByTrackingNumber(String trackingNumber);
}
