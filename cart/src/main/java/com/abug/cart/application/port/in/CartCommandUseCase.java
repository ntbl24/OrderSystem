package com.abug.cart.application.port.in;

import com.abug.cart.domain.CartId;
import com.abug.cart.domain.UserId;

public interface CartCommandUseCase {
    void deleteCart(CartId id);
    void clearCart(UserId userId);
}
