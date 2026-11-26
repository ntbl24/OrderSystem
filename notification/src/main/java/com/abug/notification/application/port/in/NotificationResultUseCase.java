package com.abug.notification.application.port.in;

import com.abug.notification.domain.EventId;

public interface NotificationResultUseCase {
    void markSent(EventId id);
    void markFailed(
            EventId id,
            String error
    );
}
