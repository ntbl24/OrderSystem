package io.order.backend.product.application.port.in;

import io.order.backend.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Optional;

public interface ProductCatalogQuery {
    Optional<Snapshot> findById(ProductId id);

    java.util.Map<ProductId, Snapshot> findByIds(java.util.Collection<ProductId> ids);

    record Snapshot(ProductId id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
}
