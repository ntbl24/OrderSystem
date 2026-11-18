package com.abug.notification.application.port.out;

import com.abug.notification.application.port.in.SendEmailCommand;

import java.util.Optional;

public interface EmailPort {
    Optional<SendResult> sendEmail(SendEmailCommand command);

    record SendResult(String messageId) {}
}
