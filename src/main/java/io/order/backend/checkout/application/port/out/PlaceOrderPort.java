package io.order.backend.checkout.application.port.out;

import io.order.backend.order.domain.Order;
import io.order.backend.product.domain.ProductId;

import java.math.BigDecimal;
import java.util.List;

public interface PlaceOrderPort {
    Order place(Command command);

    record Command(String buyerId, String sellerId, List<Line> lines, BigDecimal subtotal,
                   String shippingAddress, String notes) {
        public Command { lines = List.copyOf(lines); }
    }
    record Line(ProductId productId, String productName, String sku, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}
}
