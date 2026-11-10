package com.abug.cart.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.abug.cart.application.port.in.CartQueryUseCase;
import com.abug.cart.application.port.out.CartRepository;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.user.domain.UserId;
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
