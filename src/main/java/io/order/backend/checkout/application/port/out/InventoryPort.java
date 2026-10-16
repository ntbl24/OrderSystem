package io.order.backend.checkout.application.port.out;

import java.math.BigDecimal;
import java.util.Map;

public interface InventoryPort {
    Map<String, ProductSnapshot> reserve(Map<String, Integer> quantities);

    record ProductSnapshot(String id, String sellerId, String title, String sku, BigDecimal price) {}
}
