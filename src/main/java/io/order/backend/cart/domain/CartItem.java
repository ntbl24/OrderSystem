package io.order.backend.cart.domain;

import java.math.BigDecimal;

import lombok.Data;

@Data 
public class CartItem{
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subTotal;
    
}
