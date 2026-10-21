package io.order.backend.cart.application.port.in;

import io.order.backend.cart.domain.Cart;

public interface UpdateCartUseCase {
    Cart updateCart(String id, UpdateCartCommand dto);
}
