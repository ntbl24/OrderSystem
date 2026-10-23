package io.order.backend.cart.infrastructure.web.dto;

import java.math.BigDecimal;

import io.order.backend.cart.application.port.in.CartItemData;
import io.order.backend.product.domain.ProductId;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCartItemRequest(
    @NotBlank(message = "Product ID is required")
    String productId,

    String productName,

    @NotNull (message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    @jakarta.validation.constraints.Max(100000)
    Integer quantity,

    BigDecimal unitPrice
) {
    public CartItemData toCartItemData() {
        return new CartItemData(ProductId.of(productId), quantity);
    }
}
