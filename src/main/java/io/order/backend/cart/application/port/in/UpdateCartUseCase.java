package io.order.backend.cart.application.port.in;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;

public interface UpdateCartUseCase {
    Cart updateCart(CartId id, UpdateCartCommand command);

    Cart addItemToCart(UserId userId, CartItemData item);

    Cart updateItemQuantity(UserId userId, ProductId productId, int quantity);

    Cart removeItemFromCart(UserId userId, ProductId productId);
}
