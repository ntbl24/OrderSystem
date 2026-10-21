package io.order.backend.cart.application.port.in;

import io.order.backend.product.domain.ProductId;

import java.math.BigDecimal;

public record CartItemData (ProductId productId, String productName, int quantity, BigDecimal unitPrice){
}
