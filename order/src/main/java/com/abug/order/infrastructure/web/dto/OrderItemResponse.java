package com.abug.order.infrastructure.web.dto;

import java.math.BigDecimal;

import com.abug.order.domain.OrderItem;
import lombok.Data;

@Data
public class OrderItemResponse {
    private String productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    public static OrderItemResponse from(OrderItem result) {
        var response = new OrderItemResponse();
        response.setProductId(result.getProductId().toString());
        response.setProductName(result.getProductName());
        response.setSku(result.getSku());
        response.setQuantity(result.getQuantity());
        response.setUnitPrice(result.getUnitPrice());
        response.setSubtotal(result.getSubtotal());
        return response;
    }
}
