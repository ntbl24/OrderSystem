package io.order.backend.cart.infrastructure.persistence;

import io.order.backend.cart.domain.CartItem;
import io.order.backend.product.domain.ProductId;

public final class CartItemMapper {
    private CartItemMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static CartItem toDomain(CartItemEntity entity) {
        return new CartItem(new ProductId(entity.getProductId()), entity.getProductName(), entity.getQuantity(), entity.getUnitPrice());
    }

    public static CartItemEntity toEntity(CartItem item) {
        return new CartItemEntity(item.getProductId().value(), item.getProductName(), item.getQuantity(), item.getUnitPrice(), item.getSubTotal());
    }
}
