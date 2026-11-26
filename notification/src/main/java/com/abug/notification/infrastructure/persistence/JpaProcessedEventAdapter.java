package com.abug.notification.infrastructure.persistence;

import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class JpaProcessedEventAdapter implements ProcessedEventRepository {
    private final ProcessedEventJpaRepository repository;

    @Override
    @Transactional
    public boolean enqueue(ProcessedEvent event) {
        return repository.enqueue(event.getEventId().value(), event.getRecipient(), event.getSubject(), event.getBody()) == 1;
    }

    @Override
    public List<ProcessedEvent> claimPending(int limit) {
        return repository.claimPending(limit).stream().map(ProcessedEventMapper::domain).toList();
    }

    @Override
    @Transactional
    public int markSent(EventId id) {
        return repository.markSent(id.value());
    }

    @Override
    @Transactional
    public int markFailed(EventId id, String error) {
        return repository.markFailed(id.value(), error);
    }

    @Override
    public void saveAll(List<ProcessedEvent> events) {
        repository.saveAll(events.stream().map(ProcessedEventMapper::entity).toList());
    }
}
