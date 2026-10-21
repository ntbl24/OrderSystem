package io.order.backend.cart.application.usecase;

import io.order.backend.cart.application.port.out.CartRepository;
import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.common.application.port.CurrentBuyerPort;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.user.domain.UserId;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public class CartAccessService {
    private final CurrentBuyerPort buyer;
    private final CartRepository cartRepository;


    public void owner(UserId id) {
        UserId buyerId = UserId.of(buyer.buyerId());
        if (!buyerId.equals(id))
            throw new ResourceNotFoundException("Cart not found");
    }

    public Cart owned(Cart cart) {
        owner(cart.getUserId());
        return cart;
    }

    public Cart byId(CartId id) {
        return owned(cartRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Cart not found")));
    }

    public Cart byUser(String id) {
        owner(id);
        return cartRepository.findByUserId(id).orElseGet(() -> {
            var c = new Cart();
            c.setUserId(id);
            return c;
        });
    }
}
