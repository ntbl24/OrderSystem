package io.order.backend.cart.infrastructure.persistence;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;

public final class CartPersistenceMapper {
    private CartPersistenceMapper() {}

    public static Cart domain(CartEntity entity) {
        return Cart.restore(new CartId(entity.getId()), new UserId(entity.getUserId()), entity.getVersion(),
                entity.getItems().stream().map(CartItemMapper::toDomain).toList(),
                entity.getShippingCost(), entity.getTax(), entity.getCouponCode(), entity.getDiscount());
    }

    public static void copy(Cart cart, CartEntity entity) {
        entity.update(cart.getUserId().value(), cart.getSubTotal(), cart.getShippingCost(), cart.getTotal(), cart.getTax(), cart.getCouponCode(), cart.getDiscount());
        entity.clearItem();
        cart.getItems().forEach(i -> entity.addItem(CartItemMapper.toEntity(i)));
    }
}
