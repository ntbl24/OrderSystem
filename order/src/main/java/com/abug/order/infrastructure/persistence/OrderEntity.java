package com.abug.order.infrastructure.persistence;

import com.abug.order.domain.OrderStatus;
import com.abug.order.domain.PaymentStatus;
import com.abug.order.domain.ReservationStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "purchase_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderEntity {
    @Id
    @Column(length = 36)
    private UUID id;
    @CreationTimestamp
    @Column(updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    private Instant updatedAt;
    private boolean deleted;
    private UUID userId;
    private String orderNumber;
    @ElementCollection
    @CollectionTable(name = "order_item", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_index")
    private List<OrderLine> items = new ArrayList<>();
    @Column(precision = 19, scale = 0)
    private BigDecimal subtotal;
    @Column(precision = 19, scale = 0)
    private BigDecimal shippingCost;
    @Column(length = 2000)
    private String shippingAddress;
    private String paymentMethod;
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private PaymentStatus paymentStatus;
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private OrderStatus orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    @Column(length = 2000)
    private String notes;
    private String sellerId;
    private Instant expiresAt;
    private UUID paymentId;
    private UUID reviewPaymentId;
    @Enumerated(EnumType.STRING)
    @Column(length = 32)
    private ReservationStatus reservationStatus;
    @Version
    private Long version;

    OrderEntity(UUID id,UUID userId,String orderNumber,BigDecimal subtotal,BigDecimal shippingCost,String shippingAddress,String paymentMethod,PaymentStatus paymentStatus,OrderStatus orderStatus,LocalDateTime orderDate,LocalDateTime estimatedDeliveryDate,String notes,String sellerId,Instant expiresAt,UUID paymentId,UUID reviewPaymentId,ReservationStatus reservationStatus){
        this.id = id;
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.notes = notes;
        this.sellerId = sellerId;
        this.expiresAt = expiresAt;
        this.paymentId = paymentId;
        this.reviewPaymentId = reviewPaymentId;
        this.reservationStatus = reservationStatus;
    }

    public void update(
        UUID userId,
        String orderNumber,
        BigDecimal subtotal,
        BigDecimal shippingCost,
        String shippingAddress,
        String paymentMethod,
        PaymentStatus paymentStatus,
        OrderStatus orderStatus,
        LocalDateTime orderDate,
        LocalDateTime estimatedDeliveryDate,
        String notes,
        String sellerId,
        Instant expiresAt,
        UUID paymentId,
        UUID reviewPaymentId,
        ReservationStatus reservationStatus
    ){
        this.userId = userId;
        this.orderNumber = orderNumber;
        this.subtotal = subtotal;
        this.shippingCost = shippingCost;
        this.shippingAddress = shippingAddress;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.orderStatus = orderStatus;
        this.orderDate = orderDate;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.notes = notes;
        this.sellerId = sellerId;
        this.expiresAt = expiresAt;
        this.paymentId = paymentId;
        this.reviewPaymentId = reviewPaymentId;
        this.reservationStatus = reservationStatus;
    }

    void clearItems(){
        this.items.clear();
    }

    void addItem(OrderLine line){
        this.items.add(line);
    }

    void delete(){
        this.deleted = true;
    }
}
