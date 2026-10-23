package io.order.backend.product.infrastructure.web.dto;

import java.math.BigDecimal;
import io.order.backend.product.domain.Product;

public record ProductResponse(
        String title,
        String category,
        String description,
        BigDecimal price,
        int stock,
        String sku,
        String id,
        String sellerId
) {
    public static ProductResponse from(Product value) {
        return new ProductResponse(value.getTitle(), value.getCategory(), value.getDescription(), value.getPrice(), value.getStock(), value.getSku(), value.getId().value().toString(), value.getSellerId());
    }
}
