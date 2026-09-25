package io.order.backend.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data 
public class ProductDTO {
    @NotBlank(message = "Product name is required")
    private String name;
    @NotBlank(message = "Category is required")
    private String category;
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Price is required")
    @Positive (message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @Positive (message = "Stock must be greater than 0")
    private int stock;

    @NotBlank(message = "SKU is required")
    private String sku;
}
