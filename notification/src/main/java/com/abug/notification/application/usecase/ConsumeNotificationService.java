package com.abug.notification.application.usecase;

import com.abug.event.dto.NotificationEvent;
import com.abug.notification.application.port.in.ConsumeNotificationUseCase;
import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.application.port.in.SendEmailCommand;
import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.DeliveryStatus;
import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsumeNotificationService implements ConsumeNotificationUseCase {

    private final ProcessedEventRepository processedEventRepository;
    @Override
    public void consume(NotificationEvent event) {
        EventId eventId = EventId.of(event.getEventId());

        processedEventRepository.enqueue(ProcessedEvent.create(eventId,event.getRecipient(), event.getSubject(), event.getBody(), DeliveryStatus.PENDING, null));
    }
}
