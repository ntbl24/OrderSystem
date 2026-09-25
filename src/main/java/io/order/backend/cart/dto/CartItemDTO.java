package io.order.backend.cart.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data 
public class CartItemDTO {
    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    @NotNull (message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull (message = "Unit price is required")
    @Min(value = 0, message = "Unit price must be at least 0")
    private BigDecimal unitPrice;

    private BigDecimal subTotal;    
}
