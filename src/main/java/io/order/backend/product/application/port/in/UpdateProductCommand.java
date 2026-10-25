package io.order.backend.product.application.port.in;

import java.math.BigDecimal;

public record UpdateProductCommand(
        String title,
        String category,
        String description,
        BigDecimal price,
        int stock,
        String sku,
        String sellerId
) {}
