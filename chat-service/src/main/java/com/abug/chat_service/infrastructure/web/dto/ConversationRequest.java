package com.abug.chat_service.infrastructure.web.dto;

import com.abug.chat_service.domain.ConversationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConversationRequest(
        ConversationType type,

        @Size(min = 1)
        @NotNull
        List<String> participantIds
) {
}
