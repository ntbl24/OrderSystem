package com.abug.cart.application.port.in;

import com.abug.product.domain.ProductId;

public record CartItemData(ProductId productId, int quantity) {
}
