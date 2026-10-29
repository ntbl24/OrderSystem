package io.order.backend.order.application.port.in;

import java.util.List;

import io.order.backend.order.domain.Order;
import io.order.backend.order.domain.OrderId;
import io.order.backend.order.domain.OrderNumber;
import io.order.backend.user.domain.UserId;

public interface OrderUseCase {
    Order getOrderById(OrderId id);
    Order getOrderByOrderNumber(OrderNumber number);
    List<Order> getOrdersByUserId(UserId userId);
    Order cancel(OrderId id);
}
