package io.order.backend.payment.domain;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public record PaymentId(UUID value) {
    public PaymentId {
        Objects.requireNonNull(value, "PaymentId value cannot be null");
    }

    public static PaymentId generate() {
        return new PaymentId(UUID.randomUUID());
    }

    public static PaymentId of(String value) {
        Objects.requireNonNull(value, "PaymentId value cannot be null");
        try {
            return new PaymentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return new PaymentId(UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static PaymentId of(UUID value) {
        return new PaymentId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
