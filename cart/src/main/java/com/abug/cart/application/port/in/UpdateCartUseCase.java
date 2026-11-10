package com.abug.cart.application.port.in;

import com.abug.cart.domain.Cart;
import com.abug.cart.domain.CartId;
import com.abug.cart.domain.ProductId;
import com.abug.cart.domain.UserId;

public interface UpdateCartUseCase {
    Cart updateCart(CartId id, UpdateCartCommand command);

    Cart addItemToCart(UserId userId, CartItemData item);

    Cart updateItemQuantity(UserId userId, ProductId productId, int quantity);

    Cart removeItemFromCart(UserId userId, ProductId productId);
}
