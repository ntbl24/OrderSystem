package io.order.backend.product.domain;

import java.math.BigDecimal;

import io.order.backend.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class Product extends BaseEntity {
    private String title;
    private String category;
    private String description;
    private BigDecimal price;
    private int stock;
    private String sku;
}
