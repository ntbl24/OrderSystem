package io.order.backend.product.infrastructure.web.dto;

import io.order.backend.product.application.port.in.ProductCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock is required")
    @jakarta.validation.constraints.PositiveOrZero(message = "Stock must not be negative")
    private int stock;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String id;

    @NotBlank(message = "Seller ID is required")
    private String sellerId;

    public ProductCommand toCommand() {
        return new ProductCommand(title, category, description, price, stock, sku, sellerId);
    }
}
