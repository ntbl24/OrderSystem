package io.order.backend.cart.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class CartItemEntity {
    public String productId;
    public String productName;
    public int quantity;
    @Column(precision = 19, scale = 0)
    public BigDecimal unitPrice;
    @Column(precision = 19, scale = 0)
    public BigDecimal subTotal;
}
