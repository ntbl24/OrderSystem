package com.abug.notification.application.port.in;


public record SendEmailCommand(
        String name,
        String email,
        String subject,
        String htmlContent
) {
}
