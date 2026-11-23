package com.abug.identity.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class OutboxEventEntity {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "aggregate_id", nullable = false)
    private UUID aggregateId;

    @Column(name = "aggregate_type", nullable = false)
    private String aggregateType;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", nullable = false)
    private String payload;

    @Column(name = "published")
    private boolean published = false;

    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    private Instant createdAt;

    public static OutboxEventEntity create(UUID id, UUID aggregateId, String aggregateType, String eventType, String payload, boolean published) {
        OutboxEventEntity outboxEvent = new OutboxEventEntity();
        outboxEvent.id = id;
        outboxEvent.aggregateId = aggregateId;
        outboxEvent.aggregateType = aggregateType;
        outboxEvent.eventType = eventType;
        outboxEvent.payload = payload;
        outboxEvent.published = published;
        return outboxEvent;
    }
}
