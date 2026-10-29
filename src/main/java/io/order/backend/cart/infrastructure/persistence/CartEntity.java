package io.order.backend.cart.infrastructure.persistence;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "cart")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CartEntity {
    @Id
    @Column(updatable = false, nullable = false)
    private UUID id;

    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    private boolean deleted;
    private UUID userId;
    @Version
    private Long version;
    @ElementCollection
    @CollectionTable(name = "cart_item", joinColumns = @JoinColumn(name = "cart_id"))
    @OrderColumn(name = "line_index")
    private List<CartItemEntity> items = new ArrayList<>();
    @Column(precision = 19, scale = 0)
    private BigDecimal subTotal;
    @Column(precision = 19, scale = 0)
    private BigDecimal shippingCost;
    @Column(precision = 19, scale = 0)
    private BigDecimal total;
    @Column(precision = 19, scale = 0)
    private BigDecimal tax;
    private String couponCode;
    @Column(precision = 19, scale = 0)
    private BigDecimal discount;

    CartEntity(UUID id,UUID userId,BigDecimal subTotal,BigDecimal shippingCost,BigDecimal total,BigDecimal tax,String couponCode,BigDecimal discount){
        this.id = id;
        this.userId = userId;
        this.subTotal = subTotal;
        this.shippingCost = shippingCost;
        this.total = total;
        this.tax = tax;
        this.couponCode = couponCode;
        this.discount = discount;
    }

    void addItem(CartItemEntity item){
        this.items.add(item);
    }

    void clearItem(){
        this.items.clear();
    }

    void update(UUID userId, BigDecimal subTotal, BigDecimal shippingCost, BigDecimal total, BigDecimal tax, String couponCode, BigDecimal discount) {
        this.userId = userId;
        this.subTotal = subTotal;
        this.shippingCost = shippingCost;
        this.total = total;
        this.tax = tax;
        this.couponCode = couponCode;
        this.discount = discount;
    }

}
