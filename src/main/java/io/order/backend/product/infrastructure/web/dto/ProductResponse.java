package io.order.backend.product.infrastructure.web.dto;

import io.order.backend.product.domain.Product;
import java.math.BigDecimal;

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
        if (value == null) {
            return null;
        }
        return new ProductResponse(
                value.getTitle(),
                value.getCategory(),
                value.getDescription(),
                value.getPrice(),
                value.getStock(),
                value.getSku(),
                value.getId() != null ? value.getId().value().toString() : null,
                value.getSellerId()
        );
    }
}
