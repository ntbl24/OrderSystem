package io.order.backend.cart.application.port.in;

import io.order.backend.cart.domain.Cart;

public interface CreateCartUseCase {
    Cart createCart(CreateCartCommand command);
}
