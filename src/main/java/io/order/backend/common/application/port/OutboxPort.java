package io.order.backend.common.application.port;

public interface OutboxPort {
    void enqueue(String eventType, String aggregateId, String payload);
}
