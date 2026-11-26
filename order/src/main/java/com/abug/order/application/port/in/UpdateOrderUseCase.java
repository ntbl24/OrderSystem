package com.abug.order.application.port.in;

import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.OrderId;

public interface UpdateOrderUseCase {
    Order cancelOrder(OrderId id);
}
