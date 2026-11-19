package com.abug.identity.domain;


import java.util.Set;
import java.util.UUID;

public class OutboxEvent {
    private Long id;
    private UUID aggregateId;
    private String aggregateType;
    private String eventType;
    private String payload;
    private boolean published;

    private OutboxEvent(Long id, UUID aggregateId, String aggregateType, String eventType, String payload, boolean published) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.aggregateType = aggregateType;
        this.eventType = eventType;
        this.payload = payload;
        this.published = published;
    }

    public static OutboxEvent create(UUID aggregateId, String aggregateType, String eventType, String payload, boolean published){
        return new OutboxEvent(null, aggregateId, aggregateType, eventType, payload, published);
    }

    public Long getId() {
        return id;
    }

    public UUID getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return aggregateType;
    }


    public String getEventType() {
        return eventType;
    }

    public String getPayload() {
        return payload;
    }

    public boolean isPublished(){
        return published;
    }

    public void markPublished() {
        this.published = true;
    }
}
