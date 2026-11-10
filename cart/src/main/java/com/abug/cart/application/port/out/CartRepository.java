package com.abug.cart.application.port.out;

import java.util.Optional;

import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.user.domain.UserId;

public interface CartRepository {
    Optional<Cart> findByUserId(UserId userId);

    void deleteByUserId(UserId userId);

    Cart save(Cart cart);

    boolean existsById(CartId id);

    Optional<Cart> findById(CartId id);

    void deleteById(CartId id);
}
