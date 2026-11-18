package com.abug.notification.infrastructure.web.dto;

import com.abug.notification.application.port.in.SendEmailCommand;

public record SendEmailRequest(
        String name,
        String email,
        String subject,
        String htmlContent
) {
    public SendEmailCommand toCommand(){
        return new SendEmailCommand(name, email, subject, htmlContent);
    }
}
