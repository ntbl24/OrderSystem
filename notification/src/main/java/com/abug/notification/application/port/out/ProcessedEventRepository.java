package com.abug.notification.application.port.out;

import com.abug.notification.domain.ProcessedEvent;

public interface ProcessedEventRepository {
    void registerIfAbsent(ProcessedEvent event);
}
