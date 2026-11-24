package com.abug.notification.application.usecase;

import com.abug.event.dto.NotificationEvent;
import com.abug.notification.application.port.in.DeliverNotificationUseCase;
import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.application.port.in.SendEmailCommand;
import com.abug.notification.application.port.out.ProcessedEventRepository;
import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliverNotificationService implements DeliverNotificationUseCase {

    private final ProcessedEventRepository processedEventRepository;
    private final EmailUseCase emailUseCase;
    @Override
    public void deliver(NotificationEvent event) {
        EventId eventId = EventId.of(event.getEventId());

        try{
            processedEventRepository.registerIfAbsent(ProcessedEvent.create(eventId, Instant.now()));

            emailUseCase.sendEmail(new SendEmailCommand(null ,event.getRecipient(), event.getSubject(), event.getBody()));
        }catch (Exception e){

        }


    }
}
