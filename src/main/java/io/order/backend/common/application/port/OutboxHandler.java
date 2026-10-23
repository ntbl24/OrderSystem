package io.order.backend.common.application.port;

public interface OutboxHandler {
    String eventType();
    void handle(String eventId, String aggregateId, String payload);
}
