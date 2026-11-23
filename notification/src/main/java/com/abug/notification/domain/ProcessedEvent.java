package com.abug.notification.domain;

import java.time.Instant;

public class ProcessedEvent {
    private EventId eventId;
    private Instant processedAt;

    private ProcessedEvent (EventId eventId, Instant processedAt){
        this.eventId = eventId;
        this.processedAt = processedAt;
    }

    public static ProcessedEvent create(
            EventId eventId,
            Instant processedAt
    ) {
        return new ProcessedEvent(eventId, processedAt);
    }

    public EventId getEventId() {
        return eventId;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }
}
