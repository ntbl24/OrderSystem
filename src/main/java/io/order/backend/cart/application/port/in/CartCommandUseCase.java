package io.order.backend.cart.application.port.in;

import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;

public interface CartCommandUseCase {
    void deleteCart(CartId id);
    void clearCart(UserId userId);
}
