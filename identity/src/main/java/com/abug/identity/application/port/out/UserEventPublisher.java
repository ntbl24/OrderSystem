package com.abug.identity.application.port.out;

import com.abug.event.dto.NotificationEvent;

public interface UserEventPublisher {
    void publish(NotificationEvent event);
}
