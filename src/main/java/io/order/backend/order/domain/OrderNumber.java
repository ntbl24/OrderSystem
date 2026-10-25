package io.order.backend.order.domain;

import java.util.Objects;

public record OrderNumber(String value) {
    public OrderNumber {
        Objects.requireNonNull(value, "Order number cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Order number cannot be blank");
        }
    }

    public static OrderNumber of(String value) {
        return new OrderNumber(value);
    }

    public static OrderNumber fromOrderId(OrderId orderId) {
        return new OrderNumber("ORD-" + orderId.value());
    }

    @Override
    public String toString() {
        return value;
    }
}
