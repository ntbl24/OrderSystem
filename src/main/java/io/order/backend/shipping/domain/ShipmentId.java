package io.order.backend.shipping.domain;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public record ShipmentId(UUID value) {
    public ShipmentId {
        Objects.requireNonNull(value, "ShipmentId value cannot be null");
    }

    public static ShipmentId generate() {
        return new ShipmentId(UUID.randomUUID());
    }

    public static ShipmentId of(String value) {
        Objects.requireNonNull(value, "ShipmentId value cannot be null");
        try {
            return new ShipmentId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return new ShipmentId(UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static ShipmentId of(UUID value) {
        return new ShipmentId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
