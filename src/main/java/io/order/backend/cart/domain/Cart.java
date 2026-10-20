package io.order.backend.cart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.order.backend.common.domain.BaseEntity;
import io.order.backend.common.exception.ResourceNotFoundException;
import io.order.backend.product.domain.ProductId;
import io.order.backend.user.domain.UserId;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class Cart{
    private CartId id;
    private UserId userId;
    private Long version;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private String couponCode;
    private BigDecimal discount = BigDecimal.ZERO;

    public static Cart create(){
        Cart c = new Cart();
        c.setUserId(userId);
        return c;
    }

    public void addItem(ProductId productId, String productName, int quantity, BigDecimal unitPrice){
         if (quantity < 1 || quantity > 100000)
            throw new IllegalArgumentException("Invalid quantity");
        items.add(new CartItem(productId, productName, quantity, unitPrice));
    }
}


