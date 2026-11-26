package com.abug.order.application.port.in;

import java.util.List;

import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.valueobject.OrderNumber;
import com.abug.order.domain.UserId;

public interface OrderQueryUseCase {
    Order getOrderById(OrderId id);
    Order getOrderByOrderNumber(OrderNumber number);
    List<Order> getOrdersByUserId(UserId userId);
}
