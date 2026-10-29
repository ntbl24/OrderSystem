package io.order.backend.order.domain;

import java.math.BigDecimal;

import io.order.backend.product.domain.ProductId;

public class OrderItem{
    private ProductId productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;

    public OrderItem(ProductId productId, String productName, String sku, int quantity, BigDecimal unitPrice) {
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public ProductId getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public String getSku() {
        return sku;
    }
    public int getQuantity() {
        return quantity;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
