package io.order.backend.order.infrastructure.persistence;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "purchase_order")
public class OrderEntity {
    @Id
    @Column(length = 36)
    public String id;
    public LocalDateTime createdAt;
    public LocalDateTime updatedAt;
    public boolean deleted;
    public String userId;
    public String orderNumber;
    @ElementCollection
    @CollectionTable(name = "order_item", joinColumns = @JoinColumn(name = "order_id"))
    @OrderColumn(name = "line_index")
    public List<OrderLine> items = new ArrayList<>();
    @Column(precision = 19, scale = 0)
    public BigDecimal subtotal;
    @Column(precision = 19, scale = 0)
    public BigDecimal shippingCost;
    @Column(length = 2000)
    public String shippingAddress;
    public String paymentMethod;
    public String paymentStatus;
    public String orderStatus;
    public LocalDateTime orderDate;
    public LocalDateTime estimatedDeliveryDate;
    @Column(length = 2000)
    public String notes;
    public String sellerId;
    public java.time.Instant expiresAt;
    public String paymentId;
    public String reviewPaymentId;
    public String reservationStatus;
    @Version
    public Long version;
}
