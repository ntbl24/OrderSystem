package io.order.backend.cart.infrastructure.web.dto;

import io.order.backend.cart.domain.CartItem;
import java.math.BigDecimal;

public record CartItemResponse(String productId, String productName, int quantity,
                               BigDecimal unitPrice, BigDecimal subTotal) {
    public static CartItemResponse from(CartItem item) {
        return new CartItemResponse(item.getProductId().value().toString(), item.getProductName(),
                item.getQuantity(), item.getUnitPrice(), item.getSubTotal());
    }
}
