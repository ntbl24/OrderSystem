package com.abug.chat_service.application.dto;

import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;

public record QueryChatMessagesResult(
        UserId currentUser,
        List<ChatMessage> chatMessages
) {
}
