package com.abug.notification.infrastructure.persistence;

import com.abug.notification.domain.DeliveryStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ProcessedEventEntity {
    @Id
    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeliveryStatus status;

    @Column(name = "processed_at")
    private Instant processAt;

    @Column(name = "created_at")
    @CreationTimestamp
    private Instant createdAt;

    @Column(columnDefinition = "TEXT")
    private String lastError;

    public ProcessedEventEntity(UUID eventId, String recipient, String subject, String body, DeliveryStatus status, Instant processAt) {
        this.eventId = eventId;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.processAt = processAt;
    }
}
