package io.order.backend.order.application.port.out;

import java.util.List;

import io.order.backend.product.domain.ProductId;

public interface InventoryPort {
    void settle(List<Line> lines, String target);

    record Line(ProductId productId, int quantity) {
    }
}
