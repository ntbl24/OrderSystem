package com.abug.notification.infrastructure.web.dto;

import com.abug.notification.application.dto.SendEmailResult;

public record SendEmailResponse(
        String messageId
) {
    public static SendEmailResponse from(SendEmailResult result){
        return new SendEmailResponse(result.messageId());
    }
}
