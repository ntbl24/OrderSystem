package io.order.backend.shipping.domain;

import java.util.Objects;

public record TrackingNumber(String value) {
    public TrackingNumber {
        Objects.requireNonNull(value, "Tracking number cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Tracking number cannot be blank");
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
