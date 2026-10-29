package io.order.backend.order.application.port.in;

import java.math.BigDecimal;
import java.util.List;

import io.order.backend.order.domain.Order;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;

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
