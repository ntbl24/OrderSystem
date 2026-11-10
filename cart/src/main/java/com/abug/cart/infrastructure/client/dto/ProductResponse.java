package com.abug.cart.infrastructure.client.dto;

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
) { }
