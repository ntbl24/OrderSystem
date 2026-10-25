package io.order.backend.product.domain;

import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {
    public ProductId {
        Objects.requireNonNull(value, "ProductId value cannot be null");
    }

    public static ProductId generate() {
        return new ProductId(UUID.randomUUID());
    }

    public static ProductId of(String value) {
        Objects.requireNonNull(value, "ProductId value cannot be null");
        try {
            return new ProductId(UUID.fromString(value));
        } catch (IllegalArgumentException e) {
            return new ProductId(UUID.nameUUIDFromBytes(value.getBytes(StandardCharsets.UTF_8)));
        }
    }

    public static ProductId of(UUID value) {
        return new ProductId(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
