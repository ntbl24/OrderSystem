package io.order.backend.order.infrastructure.persistence;

import io.order.backend.order.domain.*;
import io.order.backend.user.domain.UserId;
import io.order.backend.order.application.port.out.OrderRepository;

import java.time.Instant;
import java.util.*;

import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaOrderAdapter implements OrderRepository {
    private final OrderJpaRepository repository;

    public Optional<Order> findById(OrderId id) {
        return repository.findById(id.value()).filter(e -> !e.isDeleted()).map(OrderPersistenceMapper::domain);
    }

    public boolean existsById(OrderId id) {
        return findById(id).isPresent();
    }

    public void deleteById(OrderId id) {
        repository.findById(id.value()).ifPresent(e -> e.delete());
    }

    public Order save(Order d) {
        var e = repository.findById(d.getId().value()).orElseGet(OrderEntity::new);
        if (e.getVersion() != null && !Objects.equals(e.getVersion(), d.getVersion()))
            throw new ObjectOptimisticLockingFailureException(OrderEntity.class, d.getId().value());
        OrderPersistenceMapper.copy(d, e);
        return OrderPersistenceMapper.domain(repository.saveAndFlush(e));
    }

    public Optional<Order> lockById(OrderId id) {
        return repository.lockById(id.value()).map(OrderPersistenceMapper::domain);
    }

    public List<Order> findByUserId(UserId userId) {
        return repository.findTop100ByUserIdOrderByCreatedAtDesc(userId.value()).stream()
                .map(OrderPersistenceMapper::domain)
                .toList();
    }

    public Optional<Order> findByOrderNumber(OrderNumber number) {
        return repository.findByOrderNumber(number.value()).map(OrderPersistenceMapper::domain);
    }

    public List<Order> findByOrderStatus(String status) {
        return repository.findTop100ByOrderStatus(status).stream().map(OrderPersistenceMapper::domain).toList();
    }

    public List<OrderId> expiredIds(Instant now) {
        return repository.findTop100ByOrderStatusAndExpiresAtLessThanEqualOrderById("UNPAID", now).stream()
                .map(e -> OrderId.of(e.getId())).toList();
    }
}
