package io.order.backend.order.application.port.in;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderResult {
    private String userId;
    private String id;
    private String sellerId;
    private java.time.Instant expiresAt;
    private String orderNumber;
    private List<OrderItemData> items;
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
    public static OrderResult from(io.order.backend.order.domain.Order order) {
        var result = new OrderResult();
        result.setId(order.getId());
        result.setUserId(order.getUserId());
        result.setSellerId(order.getSellerId());
        result.setExpiresAt(order.getExpiresAt());
        result.setOrderNumber(order.getOrderNumber());
        result.setItems(order.getItems() == null ? null : order.getItems().stream().map(item -> {
            var line = new OrderItemData();
            line.setProductId(item.getProductId());
            line.setProductName(item.getProductName());
            line.setSku(item.getSku());
            line.setQuantity(item.getQuantity());
            line.setUnitPrice(item.getUnitPrice());
            line.setSubtotal(item.getSubtotal());
            return line;
        }).toList());
        result.setSubtotal(order.getSubtotal());
        result.setShippingCost(order.getShippingCost());
        result.setTotal(order.getTotal());
        result.setShippingAddress(order.getShippingAddress());
        result.setPaymentMethod(order.getPaymentMethod());
        result.setPaymentStatus(order.getPaymentStatus());
        result.setOrderStatus(order.getOrderStatus());
        result.setOrderDate(order.getOrderDate());
        result.setEstimatedDeliveryDate(order.getEstimatedDeliveryDate());
        result.setNotes(order.getNotes());
        return result;
    }
}
