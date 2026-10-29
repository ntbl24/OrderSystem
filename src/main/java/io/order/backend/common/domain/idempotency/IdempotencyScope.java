package io.order.backend.common.domain.idempotency;

/** Identifies a request within one buyer and operation, never across buyers. */
public record IdempotencyScope(String buyerId, String operation, String key) {
    public IdempotencyScope {
        if (buyerId == null || buyerId.isBlank() || operation == null || operation.isBlank()
                || key == null || !key.matches("[A-Za-z0-9._:-]{1,128}")) {
            throw new IllegalArgumentException("Invalid idempotency scope or key");
        }
    }
}
