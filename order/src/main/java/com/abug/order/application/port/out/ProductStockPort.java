package com.abug.order.application.port.out;

import com.abug.order.domain.valueobject.OrderId;
import com.abug.order.domain.valueobject.ProductId;

import java.util.List;


public interface ProductStockPort {
    void settle(OrderId orderId, List<Line> lines, String target);

    record Line(ProductId productId, int quantity) {
    }
}
