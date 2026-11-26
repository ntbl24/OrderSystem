package com.abug.notification.infrastructure.persistence;

import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;

public class ProcessedEventMapper {

    public static ProcessedEvent domain(ProcessedEventEntity entity){
        return ProcessedEvent.create(EventId.of(entity.getEventId()), entity.getRecipient(), entity.getSubject(), entity.getBody(), entity.getStatus(), entity.getProcessAt());
    }

    public  static  ProcessedEventEntity entity(ProcessedEvent domain){
        return new ProcessedEventEntity(domain.getEventId().value(), domain.getRecipient(), domain.getSubject(), domain.getBody(), domain.getStatus(), domain.getProcessedAt());
    }
}
