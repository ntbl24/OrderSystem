package io.order.backend.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;



@Data 
public class OrderDTO {
    @NotNull(message = "User ID is required")
    private String userId;

    @NotBlank (message = "Order number is required")
    private String orderNumber;

    @NotEmpty (message = "Order must be contain at least one item")
    private List<OrderItemDTO> items;
    
    @NotNull(message = "Subtotal is required")
    private BigDecimal subtotal;
    
    @NotNull(message = "Shipping cost is required")
    private BigDecimal shippingCost;

    @NotNull(message = "Total is required")
    private BigDecimal total;
    
    @NotBlank(message = "Shipping address is required")
    private String shippingAddress;

    @NotBlank(message = "Payment method is required")
    private String paymentMethod;
    
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
}
