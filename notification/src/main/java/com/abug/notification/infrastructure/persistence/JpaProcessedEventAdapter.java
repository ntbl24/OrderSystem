package com.abug.notification.infrastructure.persistence;

import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Repository
public class JpaProcessedEventAdapter implements ProcessedEventRepository {
    private final ProcessedEventJpaRepository repository;

    @Override
    @Transactional
    public void registerIfAbsent(ProcessedEvent event) {
        repository.registerIfAbsent(event.getEventId().value());
    }
}
