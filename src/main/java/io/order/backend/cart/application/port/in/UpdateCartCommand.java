package io.order.backend.cart.application.port.in;

import java.util.List;

public record UpdateCartCommand(List<CartItemData> items) {
}
