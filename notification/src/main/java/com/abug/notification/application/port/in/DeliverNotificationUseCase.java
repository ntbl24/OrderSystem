package com.abug.notification.application.port.in;

import com.abug.event.dto.NotificationEvent;

public interface DeliverNotificationUseCase {
    void deliver(NotificationEvent event);
}
