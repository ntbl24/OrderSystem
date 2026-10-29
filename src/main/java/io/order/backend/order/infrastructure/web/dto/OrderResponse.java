package io.order.backend.order.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.order.backend.order.domain.Order;
import lombok.Data;

@Data
public class OrderResponse {
    private String userId;
    private String id;
    private String sellerId;
    private java.time.Instant expiresAt;
    private String orderNumber;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private String shippingAddress;
    private String paymentMethod;
    
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    public static OrderResponse from(Order result) {
        var response = new OrderResponse();
        response.setUserId(result.getUserId().toString());
        response.setId(result.getId().toString());
        response.setSellerId(result.getSellerId());
        response.setExpiresAt(result.getExpiresAt());
        response.setOrderNumber(result.getOrderNumber().toString());
        response.setItems(result.getItems() == null ? null : result.getItems().stream().map(OrderItemResponse::from).toList());
        response.setSubtotal(result.getSubtotal());
        response.setShippingCost(result.getShippingCost());
        response.setTotal(result.getTotal());
        response.setShippingAddress(result.getShippingAddress());
        response.setPaymentMethod(result.getPaymentMethod());
        response.setPaymentStatus(result.getPaymentStatus());
        response.setOrderStatus(result.getOrderStatus());
        response.setOrderDate(result.getOrderDate());
        response.setEstimatedDeliveryDate(result.getEstimatedDeliveryDate());
        response.setNotes(result.getNotes());
        return response;
    }
}
