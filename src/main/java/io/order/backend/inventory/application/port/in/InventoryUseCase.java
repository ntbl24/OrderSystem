package io.order.backend.inventory.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import io.order.backend.product.domain.ProductId;

public interface InventoryUseCase {
    Map<String, Snapshot> reserve(Map<String, Integer> quantities);
    void settle(List<Line> lines, String target);

    record Line(ProductId productId, int quantity) {}
    record Snapshot(ProductId id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
}
