package com.abug.chat_service.application.dto;

import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.UserId;

public record CreateConversationResult(
        UserId currentUser,
        Conversation conversation
) {
}
