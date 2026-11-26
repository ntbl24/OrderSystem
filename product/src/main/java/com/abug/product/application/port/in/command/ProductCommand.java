package com.abug.product.application.port.in.command;

import java.math.BigDecimal;

public record ProductCommand(
        String title,
        String category,
        String description,
        BigDecimal price,
        int stock,
        String sku,
        String sellerId
) {}
