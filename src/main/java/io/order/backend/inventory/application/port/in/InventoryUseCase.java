package io.order.backend.inventory.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface InventoryUseCase {
    Map<String, Snapshot> reserve(Map<String, Integer> quantities);
    void settle(List<Line> lines, String target);

    record Line(String productId, int quantity) {}
    record Snapshot(String id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
}
