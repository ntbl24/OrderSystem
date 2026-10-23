package io.order.backend.cart.application.port.in;

import io.order.backend.product.domain.ProductId;

public record CartItemData(ProductId productId, int quantity) {
}
