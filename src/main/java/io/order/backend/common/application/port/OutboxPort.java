package io.order.backend.common.application.port;

/** Persists an event in the caller's business transaction for durable delivery. */
public interface OutboxPort {
    void enqueue(String eventType, String aggregateId, String payload);
}
