package com.abug.notification.application.port.out;

import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import com.abug.notification.infrastructure.persistence.ProcessedEventEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProcessedEventRepository {
    boolean enqueue(ProcessedEvent event);

    List<ProcessedEvent> claimPending(int limit);

    int markSent(EventId id);
    int markFailed(EventId id, String error);

    void saveAll(List<ProcessedEvent> events);

}
