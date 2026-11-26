package com.abug.notification.application.port.in;

import com.abug.event.dto.NotificationEvent;

public interface ConsumeNotificationUseCase {
    void consume(NotificationEvent event);
}
