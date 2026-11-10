package io.order.backend.cart.infrastructure.web.dto;

import java.util.List;

import io.order.backend.cart.application.port.in.CreateCartCommand;
import io.order.backend.user.domain.UserId;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCartRequest(
    @NotBlank(message = "User ID is required")
    String userId,

    @NotNull
    @Size(max = 100)
    List<@NotNull @Valid CreateCartItemRequest> items
) {

    public CreateCartCommand toCommand() {
        return new CreateCartCommand(UserId.of(userId), items.stream().map(CreateCartItemRequest::toCartItemData).toList());
    }
}
