package com.abug.notification.infrastructure.worker;

import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.application.port.in.NotificationClaimUseCase;
import com.abug.notification.application.port.in.NotificationResultUseCase;
import com.abug.notification.application.port.in.SendEmailCommand;
import com.abug.notification.domain.EventId;
import com.abug.notification.domain.ProcessedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationWorker {

    private final NotificationClaimUseCase claimUseCase;
    private final NotificationResultUseCase resultUseCase;
    private final EmailUseCase emailUseCase;

    @Scheduled(fixedDelayString = "${notification.worker.poll-interval:1000}")
    public void process() {

        List<ProcessedEvent> deliveries =
                claimUseCase.claim(10);

        for (ProcessedEvent delivery : deliveries) {
            processOne(delivery);
        }
    }

    private void processOne(ProcessedEvent delivery) {

        EventId id = delivery.getEventId();
        try {
            emailUseCase.sendEmail(
                    new SendEmailCommand(
                            null,
                            delivery.getRecipient(),
                            delivery.getSubject(),
                            delivery.getBody()
                    )
            );

            resultUseCase.markSent(id);

            log.info("Notification delivered: {}", id);

        } catch (Exception e) {

            String error = e.getMessage() == null
                    ? e.getClass().getSimpleName()
                    : e.getMessage();

            if (error.length() > 2000) {
                error = error.substring(0, 2000);
            }

            resultUseCase.markFailed(
                    id,
                    error
            );

            log.error(
                    "Notification delivery failed: {}, attempt: {}",
                    id,
                    e
            );
        }
    }
}
