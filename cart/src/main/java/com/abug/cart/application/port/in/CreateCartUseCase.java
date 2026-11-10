package com.abug.cart.application.port.in;

import com.abug.cart.domain.Cart;

public interface CreateCartUseCase {
    Cart createCart(CreateCartCommand command);
}
