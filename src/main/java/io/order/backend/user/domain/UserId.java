package io.order.backend.user.domain;

import java.util.UUID;

public record UserId(UUID value) {
    public static UserId generate() { 
        return new UserId(UUID.randomUUID());
    }

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }
}
