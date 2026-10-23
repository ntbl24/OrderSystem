package io.order.backend.cart.domain;

import io.order.backend.product.domain.ProductId;
import java.math.BigDecimal;
import java.util.Objects;

public final class CartItem {
    private final ProductId productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;

    public CartItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice) {
        validateQuantity(quantity);
        this.productId = Objects.requireNonNull(productId);
        this.productName = Objects.requireNonNull(productName);
        if (unitPrice == null || unitPrice.signum() <= 0 || unitPrice.stripTrailingZeros().scale() > 0)
            throw new IllegalArgumentException("Invalid unit price");
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    static void validateQuantity(int quantity) {
        if (quantity < 1 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
    }

    public ProductId getProductId() { return productId; }
    public String getProductName() { return productName; }
    public int getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public BigDecimal getSubTotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
