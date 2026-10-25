package io.order.backend.payment.domain;

import java.util.Objects;

public record TransactionRef(String value) {
    public TransactionRef {
        Objects.requireNonNull(value, "Transaction ref cannot be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Transaction ref cannot be blank");
        }
    }

    public static TransactionRef of(String value) {
        return new TransactionRef(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
