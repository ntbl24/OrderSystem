package com.abug.chat_service.infrastructure.web.rest.controller.dto;

import jakarta.validation.constraints.NotBlank;

public record ChatMessageRequest(
        @NotBlank
        String conversationId,

        @NotBlank
        String message
) {
}
