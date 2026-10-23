package io.order.backend.cart.infrastructure.web.dto;

import java.util.List;

import io.order.backend.cart.application.port.in.CreateCartCommand;
import io.order.backend.user.domain.UserId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateCartRequest(
    @NotBlank(message = "User ID is required")
    String userId,

    @NotNull
    @jakarta.validation.constraints.Size(max = 100)
    List<@NotNull @jakarta.validation.Valid CreateCartItemRequest> items
) {

    public CreateCartCommand toCommand() {
        return new CreateCartCommand(UserId.of(userId), items.stream().map(CreateCartItemRequest::toCartItemData).toList());
    }
}
