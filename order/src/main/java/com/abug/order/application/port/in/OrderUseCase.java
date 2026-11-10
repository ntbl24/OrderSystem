package com.abug.order.application.port.in;

import java.util.List;

import com.abug.order.domain.Order;
import com.abug.order.domain.OrderId;
import com.abug.order.domain.OrderNumber;
import com.abug.order.domain.UserId;

public interface OrderUseCase {
    Order getOrderById(OrderId id);
    Order getOrderByOrderNumber(OrderNumber number);
    List<Order> getOrdersByUserId(UserId userId);
    Order cancel(OrderId id);
}
