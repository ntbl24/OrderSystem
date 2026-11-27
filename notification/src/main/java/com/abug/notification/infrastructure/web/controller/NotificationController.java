package com.abug.notification.infrastructure.web.controller;

import com.abug.event.dto.NotificationEvent;
import com.abug.notification.application.port.in.ConsumeNotificationUseCase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final ConsumeNotificationUseCase deliverNotificationUseCase;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent event){
        deliverNotificationUseCase.consume(event);
    }
}
