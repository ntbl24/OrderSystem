package com.abug.identity.application.port.out;

import com.abug.identity.domain.OutboxEvent;
import com.abug.identity.infrastructure.persistence.OutboxEventEntity;

import java.util.List;

public interface OutboxRepository {
    OutboxEvent save(OutboxEvent event);
    List<OutboxEvent> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}
