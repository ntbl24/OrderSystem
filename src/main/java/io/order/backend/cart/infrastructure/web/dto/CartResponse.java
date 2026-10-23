package io.order.backend.cart.infrastructure.web.dto;

import io.order.backend.cart.domain.Cart;
import java.math.BigDecimal;
import java.util.List;

public record CartResponse(String id, String userId, List<CartItemResponse> items,
                           BigDecimal subTotal, BigDecimal shippingCost, BigDecimal total,
                           BigDecimal tax, String couponCode, BigDecimal discount) {
    public static CartResponse from(Cart cart) {
        return new CartResponse(cart.getId().value().toString(), cart.getUserId().value().toString(),
                cart.getItems().stream().map(CartItemResponse::from).toList(), cart.getSubTotal(),
                cart.getShippingCost(), cart.getTotal(), cart.getTax(), cart.getCouponCode(), cart.getDiscount());
    }
}
