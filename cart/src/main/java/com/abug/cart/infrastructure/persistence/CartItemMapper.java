package com.abug.cart.infrastructure.persistence;

import com.abug.cart.domain.CartItem;
import com.abug.cart.domain.ProductId;

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
