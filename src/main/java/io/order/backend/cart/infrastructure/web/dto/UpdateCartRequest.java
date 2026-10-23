package io.order.backend.cart.infrastructure.web.dto;

import io.order.backend.cart.application.port.in.UpdateCartCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record UpdateCartRequest(@NotNull @Size(max = 100) List<@NotNull @Valid CreateCartItemRequest> items) {
    public UpdateCartCommand toCommand() {
        return new UpdateCartCommand(items.stream().map(CreateCartItemRequest::toCartItemData).toList());
    }
}
