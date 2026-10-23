package io.order.backend.order.infrastructure.persistence;

import io.order.backend.order.domain.*;
import io.order.backend.order.application.port.out.OrderRepository;

import java.util.*;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaOrderAdapter implements OrderRepository {
    private final OrderJpaRepository repository;

    public Optional<Order> findById(String id) {
        return repository.findById(id).filter(e -> !e.deleted).map(OrderPersistenceMapper::domain);
    }

    public boolean existsById(String id) {
        return findById(id).isPresent();
    }

    public void deleteById(String id) {
        repository.findById(id).ifPresent(e -> e.deleted = true);
    }

    public Order save(Order d) {
        if (d.getId() == null)
            d.setId(UUID.randomUUID().toString());
        var e = repository.findById(d.getId()).orElseGet(OrderEntity::new);
        if (e.version != null && !Objects.equals(e.version, d.getVersion()))
            throw new org.springframework.orm.ObjectOptimisticLockingFailureException(OrderEntity.class, d.getId());
        if (d.getCreatedAt() == null)
            d.setCreatedAt(java.time.LocalDateTime.now());
        d.setUpdatedAt(java.time.LocalDateTime.now());
        OrderPersistenceMapper.copy(d, e);
        return OrderPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Order> lockById(String id) {
        return repository.lockById(id).map(OrderPersistenceMapper::domain);
    }

    public List<Order> findByUserId(String userId) {
        return repository.findTop100ByUserIdOrderByCreatedAtDesc(userId).stream().map(OrderPersistenceMapper::domain)
                .toList();
    }

    public Optional<Order> findByOrderNumber(String number) {
        return repository.findByOrderNumber(number).map(OrderPersistenceMapper::domain);
    }

    public List<Order> findByOrderStatus(String status) {
        return repository.findTop100ByOrderStatus(status).stream().map(OrderPersistenceMapper::domain).toList();
    }

    public List<String> expiredIds(java.time.Instant now) {
        return repository.findTop100ByOrderStatusAndExpiresAtLessThanEqualOrderById("UNPAID", now).stream()
                .map(e -> e.id).toList();
    }
}
