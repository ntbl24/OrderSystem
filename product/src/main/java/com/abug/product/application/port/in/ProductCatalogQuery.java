package com.abug.product.application.port.in;

import com.abug.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ProductCatalogQuery {
    Snapshot findById(ProductId id);

    record Snapshot(ProductId id, String sellerId, String title, String sku, BigDecimal price, boolean deleted) {}
}
