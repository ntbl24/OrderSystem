package com.abug.product.domain;

import java.math.BigDecimal;

public record ProductDetails(
    String title,
    String category,
    String description,
    BigDecimal price,
    int stock,
    String sku,
    String sellerId
) {

}
