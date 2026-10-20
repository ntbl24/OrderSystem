package io.order.backend.cart.domain;

import java.math.BigDecimal;

import io.order.backend.product.domain.ProductId;
import lombok.Data;

@Data 
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
}
