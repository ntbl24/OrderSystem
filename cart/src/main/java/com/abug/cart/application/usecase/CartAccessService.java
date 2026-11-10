package com.abug.cart.application.usecase;

import com.abug.cart.application.port.out.CartRepository;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.common.application.port.CurrentBuyerPort;
import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;
import com.abug.user.domain.UserId;
import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class CartAccessService {
    private final CurrentBuyerPort buyer;
    private final CartRepository cartRepository;


    public void owner(UserId id) {
        UserId buyerId = UserId.of(buyer.buyerId());
        if (!buyerId.equals(id))
            throw new AppException(ErrorCode.CART_NOT_FOUND);
    }

    public Cart owned(Cart cart) {
        owner(cart.getUserId());
        return cart;
    }

    public Cart byId(CartId id) {
        return owned(cartRepository.findById(id)
        .orElseThrow(() -> new AppException(ErrorCode.CART_NOT_FOUND)));
    }

    public Cart byUser(UserId id) {
        owner(id);
        return cartRepository.findByUserId(id).orElseGet(() -> Cart.create(id));
    }
}
