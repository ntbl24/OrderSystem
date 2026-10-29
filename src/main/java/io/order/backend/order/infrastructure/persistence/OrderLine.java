package io.order.backend.order.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
@Getter
@NoArgsConstructor
public class OrderLine {
    public UUID productId;
    public String productName;
    public String sku;
    public int quantity;
    @Column(precision = 19, scale = 0)
    public BigDecimal unitPrice;
    @Column(precision = 19, scale = 0)
    public BigDecimal subtotal;

    OrderLine(UUID productId,String productName,String sku,int quantity,BigDecimal unitPrice){
        this.productId = productId;
        this.productName = productName;
        this.sku = sku;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
