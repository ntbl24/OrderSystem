package io.order.backend.product.application.port.in;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductCatalogQuery {
    Optional<Snapshot> findById(String id);

    record Snapshot(String id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
}
