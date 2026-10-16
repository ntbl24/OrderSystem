package io.order.backend.product.application.port.in;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductStockUseCase {
    Map<String, Snapshot> reserve(Map<String, Integer> quantities);
    void settle(List<Line> lines, String target);

    record Snapshot(String id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
    record Line(String productId, int quantity) {}
}
