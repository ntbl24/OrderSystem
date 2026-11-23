package com.abug.notification.infrastructure.web.mapper;

import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import com.abug.notification.infrastructure.persistence.ProcessedEventEntity;


public class ProcessedEventMapper {
    private ProcessedEventMapper(){}

    public static ProcessedEvent domain(ProcessedEventEntity entity){
        return ProcessedEvent.create(EventId.of(entity.getEventId()), entity.getProcessAt());
    }

    public static ProcessedEventEntity entity(ProcessedEvent domain){
        return new ProcessedEventEntity(domain.getEventId().value(), domain.getProcessedAt());
    }
}
