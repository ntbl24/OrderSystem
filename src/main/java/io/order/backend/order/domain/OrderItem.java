package io.order.backend.order.domain;

import java.math.BigDecimal;

public class OrderItem{
    private String productId;
    private String productName;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
