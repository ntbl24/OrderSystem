package io.order.backend.order.application.port.in;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceOrderUseCase {
    OrderResult placeOrder(CreateOrder command);

    record CreateOrder(String ownerId, String sellerId, List<Line> lines, BigDecimal subtotal,
            String shippingAddress, String notes) {
        public CreateOrder {
            lines = List.copyOf(lines);
        }
    }

    record Line(String productId, String productName, String sku, int quantity,
            BigDecimal unitPrice, BigDecimal subtotal) {
    }
}
