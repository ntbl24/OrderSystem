package com.abug.notification.application.port.in;

import com.abug.notification.domain.ProcessedEvent;

import java.util.List;

public interface NotificationClaimUseCase {
    List<ProcessedEvent> claim(int batchSize);
}
