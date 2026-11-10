package com.abug.order.application.port.out;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.abug.order.domain.Order;
import com.abug.order.domain.OrderId;
import com.abug.order.domain.OrderNumber;
import com.abug.user.domain.UserId;

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