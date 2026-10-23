package io.order.backend.cart.infrastructure.persistence;

import io.order.backend.cart.domain.CartItem;
import io.order.backend.product.domain.ProductId;

public final class CartItemMapper {
    private CartItemMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static CartItem toDomain(CartItemEntity entity) {
        return new CartItem(new ProductId(entity.productId), entity.productName, entity.quantity, entity.unitPrice);
    }

    public static CartItemEntity toEntity(CartItem item) {
        CartItemEntity entity = new CartItemEntity();
        entity.productId = item.getProductId().value();
        entity.productName = item.getProductName();
        entity.quantity = item.getQuantity();
        entity.unitPrice = item.getUnitPrice();
        return entity;
    }
}
