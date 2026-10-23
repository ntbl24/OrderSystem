package io.order.backend.cart.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.order.backend.cart.application.port.in.CartQueryUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartQueryService implements CartQueryUseCase {
    private final CartRepository carts;
    private final CartAccessService access;

    @Transactional(readOnly = true)
    public Cart getCartById(CartId id) {
        return access.byId(id);
    }

    @Transactional(readOnly = true)
    public Optional<Cart> getCartByUserId(UserId userId) {
        access.owner(userId);
        return carts.findByUserId(userId);
    }
    
}
