package io.order.backend.cart.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import io.order.backend.common.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class Cart extends BaseEntity {
    private String userId;
    private List<CartItem> items = new ArrayList<>();
    private BigDecimal subTotal = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;
    private BigDecimal total = BigDecimal.ZERO;
    private BigDecimal tax = BigDecimal.ZERO;
    private String couponCode;
    private BigDecimal discount = BigDecimal.ZERO;
}
