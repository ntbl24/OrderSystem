package com.abug.chat_service.application.dto;

import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.UserId;

public record CreateChatMessageResult(
        UserId currentUser,
        ChatMessage chatMessage
) {
}
