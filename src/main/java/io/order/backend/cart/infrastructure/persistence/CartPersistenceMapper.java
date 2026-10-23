package io.order.backend.cart.infrastructure.persistence;

import io.order.backend.cart.domain.Cart;
import io.order.backend.cart.domain.CartId;
import io.order.backend.user.domain.UserId;

public final class CartPersistenceMapper {
    private CartPersistenceMapper() {}

    public static Cart domain(CartEntity entity) {
        return Cart.restore(new CartId(entity.id), new UserId(entity.userId), entity.version,
                entity.items.stream().map(CartItemMapper::toDomain).toList(),
                entity.shippingCost, entity.tax, entity.couponCode, entity.discount);
    }

    public static void copy(Cart cart, CartEntity entity) {
        entity.id = cart.getId().value();
        entity.userId = cart.getUserId().value();
        entity.items.clear();
        entity.items.addAll(cart.getItems().stream().map(i -> {
            CartItemEntity item = new CartItemEntity();
            item.productId = i.getProductId().value();
            item.productName = i.getProductName();
            item.quantity = i.getQuantity();
            item.unitPrice = i.getUnitPrice();
            item.subTotal = i.getSubTotal();
            return item;
        }).toList());
        entity.subTotal = cart.getSubTotal();
        entity.shippingCost = cart.getShippingCost();
        entity.total = cart.getTotal();
        entity.tax = cart.getTax();
        entity.couponCode = cart.getCouponCode();
        entity.discount = cart.getDiscount();
    }
}
