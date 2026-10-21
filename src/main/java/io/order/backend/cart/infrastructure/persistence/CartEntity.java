package io.order.backend.cart.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "cart")
public class CartEntity {
    @Id
    @Column(updatable = false, nullable = false)
    public UUID id;

    public UUID userId;
    @Version
    public Long version;
    @ElementCollection
    @CollectionTable(name = "cart_item", joinColumns = @JoinColumn(name = "cart_id"))
    @OrderColumn(name = "item_number")
    public List<CartItemEntity> items = new ArrayList<>();
    @Column(precision = 19, scale = 0)
    public BigDecimal subTotal;
    @Column(precision = 19, scale = 0)
    public BigDecimal shippingCost;
    @Column(precision = 19, scale = 0)
    public BigDecimal total;
    @Column(precision = 19, scale = 0)
    public BigDecimal tax;
    public String couponCode;
    @Column(precision = 19, scale = 0)
    public BigDecimal discount;
}
