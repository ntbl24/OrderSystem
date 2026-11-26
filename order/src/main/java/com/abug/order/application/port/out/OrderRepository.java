package com.abug.order.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.valueobject.OrderNumber;
import com.abug.order.domain.OrderStatus;
import com.abug.order.domain.UserId;

public interface OrderRepository {
    List<Order> findByUserId(UserId userId);

    Optional<Order> findByOrderNumber(OrderNumber orderNumber);

    List<Order> findByOrderStatus(OrderStatus status);

    Order save(Order order);

    Optional<Order> lockById(OrderId id);

    List<OrderId> expiredIds(Instant now);

    Optional<Order> findById(OrderId id);

    boolean existsById(OrderId id);

    void deleteById(OrderId id);
}