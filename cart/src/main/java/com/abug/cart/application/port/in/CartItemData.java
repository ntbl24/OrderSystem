package com.abug.cart.application.port.in;


import com.abug.cart.domain.ProductId;

public record CartItemData(ProductId productId, int quantity) {
}
