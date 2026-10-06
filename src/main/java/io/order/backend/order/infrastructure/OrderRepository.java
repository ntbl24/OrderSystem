package io.order.backend.order.infrastructure;

import java.util.List;
import java.util.Optional;

import io.order.backend.order.domain.Order;

public interface OrderRepository {
    List<Order> findByUserId(String userId);
    Optional<Order> findByOrderNumber(String orderNumber);
    List<Order> findByOrderStatus(String status);
    Order save(Order order);
    Optional<Order> findById(String id);
    boolean existsById(String id);
    void deleteById(String id);
}