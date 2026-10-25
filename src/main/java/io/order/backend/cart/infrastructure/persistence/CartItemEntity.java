package io.order.backend.cart.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
public class CartItemEntity {
    private UUID productId;
    private String productName;
    private int quantity;
    @Column(precision = 19, scale = 0)
    private BigDecimal unitPrice;
    @Column(precision = 19, scale = 0)
    private BigDecimal subTotal;

    CartItemEntity(UUID productId,String productName,int quantity,BigDecimal unitPrice,BigDecimal subTotal){
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subTotal = subTotal;
    }
}
