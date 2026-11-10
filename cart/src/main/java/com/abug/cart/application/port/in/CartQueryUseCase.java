package com.abug.cart.application.port.in;

import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.cart.domain.UserId;

import java.util.Optional;

public interface CartQueryUseCase {
    Cart getCartById(CartId id);
    Optional<Cart> getCartByUserId(UserId userId);
}
