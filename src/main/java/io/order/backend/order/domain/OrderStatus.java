package io.order.backend.order.domain;

public enum OrderStatus {
    UNPAID,
    CANCELLED,
    EXPIRED,
    PAYMENT_REVIEW,
    PAID,
    REFUNDED
}
