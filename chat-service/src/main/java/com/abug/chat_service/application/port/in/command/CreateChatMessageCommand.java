package com.abug.chat_service.application.port.in.command;

import com.abug.chat_service.domain.ConversationId;

public record CreateChatMessageCommand(
        ConversationId conversationId,
        String message
) {
}
