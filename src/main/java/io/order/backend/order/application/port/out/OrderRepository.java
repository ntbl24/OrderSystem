package io.order.backend.order.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.user.domain.UserId;

public interface OrderRepository {
    List<Order> findByUserId(UserId userId);

    Optional<Order> findByOrderNumber(OrderNumber orderNumber);

    List<Order> findByOrderStatus(String status);

    Order save(Order order);

    Optional<Order> lockById(OrderId id);

    List<OrderId> expiredIds(Instant now);

    Optional<Order> findById(OrderId id);

    boolean existsById(OrderId id);

    void deleteById(OrderId id);
}