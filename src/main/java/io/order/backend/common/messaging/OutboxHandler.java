package io.order.backend.common.messaging;

public interface OutboxHandler {
    String eventType();
    void handle(String eventId, String aggregateId, String payload);
}
