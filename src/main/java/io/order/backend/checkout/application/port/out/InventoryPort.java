package io.order.backend.checkout.application.port.out;

import java.math.BigDecimal;
import java.util.Map;

import io.order.backend.product.domain.ProductId;

public interface InventoryPort {
    Map<String, ProductSnapshot> reserve(Map<String, Integer> quantities);

    record ProductSnapshot(ProductId id, String sellerId, String title, String sku, BigDecimal price) {}
}
