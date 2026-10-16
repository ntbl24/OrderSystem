package io.order.backend.checkout.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CheckoutRequest(@NotEmpty @Size(max = 100) List<@Valid Item> items,
        @NotBlank @Size(max = 2000) String shippingAddress, @Size(max = 2000) String notes) {
    public record Item(@NotBlank String productId, @Min(1) @Max(100000) int quantity) {
    }
}
