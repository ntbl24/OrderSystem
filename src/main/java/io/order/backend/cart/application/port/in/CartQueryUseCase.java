package io.order.backend.cart.application.port.in;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;

import java.util.Optional;

public interface CartQueryUseCase {
    Cart getCartById(CartId id);
    Optional<Cart> getCartByUserId(UserId userId);
}
