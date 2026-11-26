package com.abug.notification.domain;

import com.abug.notification.common.exception.AppException;
import com.abug.notification.common.exception.ErrorCode;

import java.time.Instant;

public class ProcessedEvent {
    private EventId eventId;
    private String recipient;
    private String subject;
    private String body;
    private DeliveryStatus status;
    private Instant processedAt;

    public ProcessedEvent(EventId eventId, String recipient, String subject, String body, DeliveryStatus status, Instant processedAt) {
        this.eventId = eventId;
        this.recipient = recipient;
        this.subject = subject;
        this.body = body;
        this.status = status;
        this.processedAt = processedAt;
    }

    public static ProcessedEvent create(
            EventId eventId, String recipient, String subject, String body, DeliveryStatus status, Instant processedAt
    ) {
        return new ProcessedEvent(eventId, recipient, subject, body, status, processedAt);
    }

    public void changeStatus(DeliveryStatus status){
        if(status.equals(DeliveryStatus.PENDING)){
            throw new AppException(ErrorCode.INVALID_INPUT);
        }
        this.status = status;
    }

    public EventId getEventId() {
        return eventId;
    }

    public DeliveryStatus getStatus() {
        return status;
    }

    public Instant getProcessedAt() {
        return processedAt;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
