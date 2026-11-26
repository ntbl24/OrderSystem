package com.abug.order.application.port.in;

import java.math.BigDecimal;
import java.util.List;

import com.abug.order.domain.Order;
import com.abug.order.domain.valueobject.ProductId;
import com.abug.order.domain.UserId;

public interface PlaceOrderUseCase {
    Order placeOrder(CreateOrder command);

    record CreateOrder(UserId ownerId, String sellerId, List<Line> lines, BigDecimal subtotal,
                       String shippingAddress, String notes) {
        public CreateOrder {
            lines = List.copyOf(lines);
        }
    }

    record Line(ProductId productId, String productName, String sku, int quantity,
                BigDecimal unitPrice, BigDecimal subtotal) {
    }
}
