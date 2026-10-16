package io.order.backend.order.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Embeddable
public class OrderLine {
    public String productId;
    public String productName;
    public String sku;
    public int quantity;
    @Column(precision = 19, scale = 0)
    public BigDecimal unitPrice;
    @Column(precision = 19, scale = 0)
    public BigDecimal subtotal;
}
