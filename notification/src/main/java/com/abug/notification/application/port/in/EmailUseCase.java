package com.abug.notification.application.port.in;

import com.abug.notification.application.dto.SendEmailResult;

public interface EmailUseCase {
    SendEmailResult sendEmail(SendEmailCommand command);
}
