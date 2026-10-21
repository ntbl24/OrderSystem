package io.order.backend.cart.application.port.out;

import java.util.Optional;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;

public interface CartRepository {
    Optional<Cart> findByUserId(UserId userId);

    void deleteByUserId(UserId userId);

    Cart save(Cart cart);

    boolean existsById(String id);

    Optional<Cart> findById(CartId id);

    void deleteById(String id);
}
