package io.order.backend.order.infrastructure.web.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemResponse {
    private String productId;
    private String productName;
    private String sku;
    private int quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    public static OrderItemResponse from(io.order.backend.order.application.port.in.OrderItemData result) {
        var response = new OrderItemResponse();
        response.setProductId(result.getProductId());
        response.setProductName(result.getProductName());
        response.setSku(result.getSku());
        response.setQuantity(result.getQuantity());
        response.setUnitPrice(result.getUnitPrice());
        response.setSubtotal(result.getSubtotal());
        return response;
    }
}
