package com.abug.order.domain.valueobject;

import java.util.Objects;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;

public record OrderNumber(String value) {
    public OrderNumber {
        Objects.requireNonNull(value, "Order number cannot be null");
        if (value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Order number cannot be blank");
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
