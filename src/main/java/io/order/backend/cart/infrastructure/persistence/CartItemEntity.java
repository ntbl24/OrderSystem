package io.order.backend.cart.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Embeddable
public class CartItemEntity {
    public UUID productId;
    public String productName;
    public int quantity;
    @Column(precision = 19, scale = 0)
    public BigDecimal unitPrice;
    @Column(precision = 19, scale = 0)
    public BigDecimal subTotal;
}
