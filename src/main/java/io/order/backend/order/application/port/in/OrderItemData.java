package io.order.backend.order.application.port.in;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemData {
    private String productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
