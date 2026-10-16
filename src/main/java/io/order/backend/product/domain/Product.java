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
    private String sellerId;
    private int reserved;
    private Long version;

    public void validate() {
        if (sellerId == null || sellerId.isBlank() || price == null || price.signum() <= 0
                || price.stripTrailingZeros().scale() > 0 || price.precision() > 19
                || stock < 0 || reserved < 0 || reserved > stock) {
            throw new IllegalArgumentException("Invalid product, integer VND price or inventory");
        }
    }

    public void reserve(int quantity) {
        if (quantity < 1 || stock - reserved < quantity) throw new IllegalStateException("Insufficient stock");
        reserved = Math.addExact(reserved, quantity);
    }

    public void release(int quantity) {
        if (quantity < 1 || reserved < quantity) throw new IllegalStateException("Invalid stock release");
        reserved -= quantity;
    }

    public void consume(int quantity) {
        release(quantity);
        stock -= quantity;
    }

    public void restock(int quantity) {
        if (quantity < 1) throw new IllegalArgumentException("Invalid quantity");
        stock = Math.addExact(stock, quantity);
    }
}
