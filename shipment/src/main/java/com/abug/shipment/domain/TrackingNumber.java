package com.abug.shipment.domain;

import java.util.Objects;

import com.abug.common.exception.AppException;
import com.abug.common.exception.ErrorCode;

public record TrackingNumber(String value) {
    public TrackingNumber {
        Objects.requireNonNull(value, "Tracking number cannot be null");
        if (value.isBlank()) {
            throw new AppException(ErrorCode.INVALID_INPUT, "Tracking number cannot be blank");
        }
    }

    public static TrackingNumber of(String value) {
        return new TrackingNumber(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
