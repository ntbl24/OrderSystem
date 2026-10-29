package io.order.backend.common.domain.idempotency;

import io.order.backend.common.exception.ConflictException;
import java.nio.charset.StandardCharsets;

public final class IdempotencyRecord {
    private final String requestHash;
    private String responseBody;

    private IdempotencyRecord(String requestHash, String responseBody) {
        this.requestHash = requestHash;
        this.responseBody = responseBody;
    }

    public static IdempotencyRecord claim(String requestHash) {
        return new IdempotencyRecord(requestHash, null);
    }

    public static IdempotencyRecord reconstitute(String requestHash, String responseBody) {
        return new IdempotencyRecord(requestHash, responseBody);
    }

    public void complete(String responseBody) {
        if (this.responseBody != null) {
            throw new IllegalStateException("Request is already completed");
        }
        if (responseBody.getBytes(StandardCharsets.UTF_8).length > 262144) {
            throw new IllegalArgumentException("Idempotent response exceeds storage limit");
        }
        this.responseBody = responseBody;
    }

    public String replay(String requestHash) {
        if (!this.requestHash.equals(requestHash)) {
            throw new ConflictException("Idempotency key was used with another request");
        }
        if (responseBody == null) {
            throw new ConflictException("Request is still in progress");
        }
        return responseBody;
    }
}
