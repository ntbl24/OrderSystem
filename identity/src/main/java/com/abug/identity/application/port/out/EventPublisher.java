package com.abug.identity.application.port.out;

import com.abug.event.dto.EventMetadata;
import com.abug.event.dto.IntegrationEvent;
import com.abug.event.dto.NotificationEvent;

public interface EventPublisher {
    void publish(
            IntegrationEvent event,
            EventMetadata metadata
    );
}
