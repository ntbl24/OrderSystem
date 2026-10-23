package io.order.backend.order.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import io.order.backend.common.domain.BaseEntity;

public class Order extends BaseEntity {
    private String userId;
    private String orderNumber;
    private List<OrderItem> items;
    private BigDecimal subtotal;
    private BigDecimal shippingCost;
    private String shippingAddress;
    private String paymentMethod;
    private String paymentStatus;
    private String orderStatus;
    private LocalDateTime orderDate;
    private LocalDateTime estimatedDeliveryDate;
    private String notes;
    private String sellerId;
    private java.time.Instant expiresAt;
    private String paymentId;
    private String reviewPaymentId;

    public static Order place(String ownerId, String sellerId, List<OrderItem> items,
            BigDecimal subtotal, String shippingAddress, String notes, java.time.Instant now,
            LocalDateTime orderDate) {
        var order = new Order();
        order.setId(java.util.UUID.randomUUID().toString());
        order.orderNumber = "ORD-" + order.getId();
        order.userId = ownerId;
        order.sellerId = sellerId;
        order.items = List.copyOf(items);
        order.subtotal = subtotal;
        order.shippingCost = BigDecimal.valueOf(30000);
        order.shippingAddress = shippingAddress;
        order.notes = notes;
        order.paymentMethod = "VNPAY";
        order.paymentStatus = "UNPAID";
        order.orderStatus = "UNPAID";
        order.reservationStatus = "HELD";
        order.orderDate = orderDate;
        order.expiresAt = now.plusSeconds(900);
        return order;
    }
    public String getReviewPaymentId() {
        return reviewPaymentId;
    }

    public void setReviewPaymentId(String value) {
        reviewPaymentId = value;
    }

    private String reservationStatus;
    private Long version;

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String value) {
        sellerId = value;
    }

    public java.time.Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(java.time.Instant value) {
        expiresAt = value;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String value) {
        paymentId = value;
    }

    public String getReservationStatus() {
        return reservationStatus;
    }

    public void setReservationStatus(String value) {
        reservationStatus = value;
    }

    public boolean requiresReservationTransition(String target) {
        if (target.equals(reservationStatus))
            return false;
        if (!("HELD".equals(reservationStatus) && ("RELEASED".equals(target) || "CONSUMED".equals(target)))
                && !("CONSUMED".equals(reservationStatus) && "RETURNED".equals(target)))
            throw new IllegalStateException("Invalid inventory transition");
        return true;
    }

    public void transitionReservation(String target) {
        if (requiresReservationTransition(target))
            reservationStatus = target;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long value) {
        version = value;
    }

    public BigDecimal getTotal() {
        return subtotal.add(shippingCost);
    }

    public boolean unpaid() {
        return "UNPAID".equals(orderStatus);
    }

    public void cancel() {
        if (!unpaid())
            throw new IllegalStateException("Only unpaid orders can be cancelled");
        orderStatus = "CANCELLED";
    }

    public boolean expire(java.time.Instant now) {
        if (!unpaid() || expiresAt.isAfter(now))
            return false;
        orderStatus = "EXPIRED";
        return true;
    }

    public boolean acceptPayment(String id, BigDecimal amount, String currency, java.time.Instant now) {
        if (id == null || id.isBlank())
            throw new IllegalArgumentException("Payment ID required");
        if (id.equals(paymentId))
            return false;
        if (!"VND".equals(currency) || amount == null || amount.compareTo(getTotal()) != 0
                || !unpaid() || !expiresAt.isAfter(now)) {
            orderStatus = "PAYMENT_REVIEW";
            paymentStatus = "REVIEW";
            reviewPaymentId = id;
            return false;
        }
        paymentId = id;
        paymentStatus = "PAID";
        orderStatus = "PAID";
        return true;
    }

    public boolean acceptRefund(String id, BigDecimal amount, String currency) {
        if (!"VND".equals(currency) || amount == null || amount.compareTo(getTotal()) != 0)
            throw new IllegalStateException("Refund amount or currency mismatch");
        if (!id.equals(paymentId) || "REFUNDED".equals(paymentStatus))
            return false;
        paymentStatus = "REFUNDED";
        orderStatus = "REFUNDED";
        return true;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public LocalDateTime getEstimatedDeliveryDate() {
        return estimatedDeliveryDate;
    }

    public void setEstimatedDeliveryDate(LocalDateTime estimatedDeliveryDate) {
        this.estimatedDeliveryDate = estimatedDeliveryDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

}
