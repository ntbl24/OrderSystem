package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.in.CartCommandUseCase;
import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;
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
