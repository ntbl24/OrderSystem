package com.abug.cart.application.usecase;

import com.abug.cart.application.port.out.CartRepository;
import com.abug.cart.application.port.out.CurrentUserPort;
import com.abug.cart.common.exception.AppException;
import com.abug.cart.common.exception.ErrorCode;
import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.cart.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartAccessService {
    private final CurrentUserPort currentUserPort;
    private final CartRepository cartRepository;


    public void owner(UserId id) {
        UserId buyerId = UserId.of(currentUserPort.currentUserId());
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
}
