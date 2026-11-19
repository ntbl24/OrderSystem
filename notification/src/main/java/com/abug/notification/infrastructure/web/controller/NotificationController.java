package com.abug.notification.infrastructure.web.controller;

import com.abug.event.dto.NotificationEvent;
import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.application.port.in.SendEmailCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationController {

    private final EmailUseCase emailUseCase;

    @KafkaListener(topics = "notification-delivery")
    public void listenNotificationDelivery(NotificationEvent message){
        emailUseCase.sendEmail(new SendEmailCommand("",message.getRecipient(), message.getSubject(), message.getBody()));
    }
}
