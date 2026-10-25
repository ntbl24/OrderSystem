package io.order.backend.shipping.infrastructure.persistence;

import io.order.backend.order.domain.OrderId;
import io.order.backend.shipping.application.port.out.ShipmentRepository;

import io.order.backend.shipping.domain.*;
import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaShipmentAdapter implements ShipmentRepository {
    private final ShipmentJpaRepository repository;

    public Optional<Shipment> findById(ShipmentId id) {
        return repository.findById(id.value()).filter(e -> !e.isDeleted()).map(ShipmentPersistenceMapper::domain);
    }

    public boolean existsById(ShipmentId id) {
        return findById(id).isPresent();
    }

    public void deleteById(ShipmentId id) {
        repository.findById(id.value()).ifPresent(e -> e.delete());
    }

    public Shipment save(Shipment d) {
        var e = repository.lockById(d.getId().value()).orElseGet(ShipmentEntity::new);
        if (e.getVersion() != null && d.getVersion() != null && !Objects.equals(e.getVersion(), d.getVersion()))
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(ShipmentEntity.class, d.getId());
        ShipmentPersistenceMapper.copy(d, e);
        return ShipmentPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Shipment> findByOrderId(OrderId orderId) {
        return repository.findByOrderId(orderId.value()).map(ShipmentPersistenceMapper::domain);
    }

    public Optional<Shipment> findByTrackingNumber(TrackingNumber trackingNumber) {
        return repository.findByTrackingNumber(trackingNumber.value()).map(ShipmentPersistenceMapper::domain);
    }
}
