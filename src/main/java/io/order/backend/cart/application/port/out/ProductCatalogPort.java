package io.order.backend.cart.application.port.out;

import io.order.backend.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Optional;

public interface ProductCatalogPort {
    Optional<Snapshot> findById(ProductId id);

    record Snapshot(ProductId id, String title, BigDecimal price) {}
}
