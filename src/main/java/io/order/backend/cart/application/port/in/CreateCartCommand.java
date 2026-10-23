package io.order.backend.cart.application.port.in;

import java.util.List;

import io.order.backend.user.domain.UserId;

public record CreateCartCommand(UserId userId, List<CartItemData> items) {}
