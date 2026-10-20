package io.order.backend.cart.domain;

import java.util.UUID;

public record CartId(UUID value) {
    public static CartId generate() { 
        return new CartId(UUID.randomUUID());
    }

    public static CartId of(String value) {
        return new CartId(UUID.fromString(value));
    }
}
