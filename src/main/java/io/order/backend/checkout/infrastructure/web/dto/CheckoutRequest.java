package io.order.backend.checkout.infrastructure.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;

public record CheckoutRequest(@NotEmpty @Size(max = 100) List<@Valid Item> items,
        @NotBlank @Size(max = 2000) String shippingAddress, @Size(max = 2000) String notes) {
    public io.order.backend.checkout.application.port.in.CheckoutCommand toCommand() {
        return new io.order.backend.checkout.application.port.in.CheckoutCommand(
                items == null ? null : items.stream().map(item -> new io.order.backend.checkout.application.port.in.CheckoutCommand.Item(item.productId(), item.quantity())).toList(),
                shippingAddress, notes);
    }

    public record Item(@NotBlank String productId, @Min(1) @Max(100000) int quantity) {
    }
}
