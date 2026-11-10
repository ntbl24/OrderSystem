package com.abug.cart.application.usecase;

import com.abug.cart.application.port.in.CartCommandUseCase;
import com.abug.cart.application.port.out.CartRepository;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.user.domain.UserId;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartCommandService implements CartCommandUseCase {
    private final CartRepository carts;
    private final CartAccessService access;

    public void deleteCart(CartId id) {
        access.byId(id);
        carts.deleteById(id);
    }

    public void clearCart(UserId userId) {
        access.owner(userId);
        carts.deleteByUserId(userId);
    }
}
