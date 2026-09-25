package io.order.backend.cart.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class CartDTO {
    private String id;

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull 
    private List<CartItemDTO> items;

    private BigDecimal subTotal;
    private BigDecimal shippingCost;
    private BigDecimal total;
    private BigDecimal tax;
    private String couponCode;
    private BigDecimal discount;
}
