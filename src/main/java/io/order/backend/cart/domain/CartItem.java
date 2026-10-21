package io.order.backend.cart.domain;

import java.math.BigDecimal;

import io.order.backend.product.domain.ProductId;

public class CartItem{
    private ProductId productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    
    public CartItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice){
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void setQuantity(int quantity){
        if (quantity < 1 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
        this.quantity = quantity;
        this.subTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    public ProductId getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getSubTotal() {
        return subTotal;
    }
}
