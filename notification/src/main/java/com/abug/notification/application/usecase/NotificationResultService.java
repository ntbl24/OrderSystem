package com.abug.notification.application.usecase;

import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.EventId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationResultService {

    private final ProcessedEventRepository repository;

    @Transactional
    public void markSent(EventId id) {
        repository.markSent(id);
    }

    @Transactional
    public void markFailed(
            EventId id,
            String error
    ) {
        repository.markFailed(
                id,
                error
        );
    }
}
