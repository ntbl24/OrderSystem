package io.order.backend.shipping.infrastructure.persistence;

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

    public Optional<Shipment> findById(String id) {
        return repository.findById(id).filter(e -> !e.deleted).map(ShipmentPersistenceMapper::domain);
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public void deleteById(String id) {
        repository.findById(id).ifPresent(e -> e.deleted = true);
    }

    public Shipment save(Shipment d) {
        if (d.getId() == null)
            d.setId(UUID.randomUUID().toString());
        var e = repository.lockById(d.getId()).orElseGet(ShipmentEntity::new);
        if (e.version != null && d.getVersion() != null && !Objects.equals(e.version, d.getVersion()))
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(ShipmentEntity.class, d.getId());
        if (d.getCreatedAt() == null)
            d.setCreatedAt(java.time.LocalDateTime.now());
        d.setUpdatedAt(java.time.LocalDateTime.now());
        d.setVersion(e.version);
        ShipmentPersistenceMapper.copy(d, e);
        return ShipmentPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Shipment> findByOrderId(String orderId) {
        return repository.findByOrderId(orderId).map(ShipmentPersistenceMapper::domain);
    }

    public Optional<Shipment> findByTrackingNumber(String number) {
        return repository.findByTrackingNumber(number).map(ShipmentPersistenceMapper::domain);
    }
}
