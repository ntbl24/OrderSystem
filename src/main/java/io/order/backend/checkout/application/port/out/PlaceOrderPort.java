package io.order.backend.checkout.application.port.out;

import io.order.backend.order.dto.OrderDTO;
import java.math.BigDecimal;
import java.util.List;

public interface PlaceOrderPort {
    OrderDTO place(Command command);

    record Command(String buyerId, String sellerId, List<Line> lines, BigDecimal subtotal,
                   String shippingAddress, String notes) {
        public Command { lines = List.copyOf(lines); }
    }
    record Line(String productId, String productName, String sku, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {}
}
