package io.order.backend.order.application.port.out;

import java.util.List;
import java.util.Optional;

import io.order.backend.order.domain.Order;

public interface OrderRepository {
    List<Order> findByUserId(String userId);

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByOrderStatus(String status);

    Order save(Order order);

    Optional<Order> lockById(String id);

    List<String> expiredIds(java.time.Instant now);

    Optional<Order> findById(String id);

    boolean existsById(String id);

    void deleteById(String id);

    default Optional<Order> findById(io.order.backend.order.domain.OrderId id) {
        return findById(id.value().toString());
    }

    default Optional<Order> lockById(io.order.backend.order.domain.OrderId id) {
        return lockById(id.value().toString());
    }

    default boolean existsById(io.order.backend.order.domain.OrderId id) {
        return existsById(id.value().toString());
    }

    default void deleteById(io.order.backend.order.domain.OrderId id) {
        deleteById(id.value().toString());
    }

    default List<Order> findByUserId(io.order.backend.user.domain.UserId userId) {
        return findByUserId(userId.value().toString());
    }

    default Optional<Order> findByOrderNumber(io.order.backend.order.domain.OrderNumber orderNumber) {
        return findByOrderNumber(orderNumber.value());
    }
}