package com.abug.cart.application.port.out;

import com.abug.cart.domain.ProductId;

import java.math.BigDecimal;
import java.util.Optional;

public interface ProductCatalogPort {
    Optional<Snapshot> findById(ProductId id);

    record Snapshot(ProductId id, String title, BigDecimal price) {}
}
