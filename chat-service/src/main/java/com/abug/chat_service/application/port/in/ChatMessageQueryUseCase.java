package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.dto.QueryChatMessagesResult;
import com.abug.chat_service.domain.ConversationId;

public interface ChatMessageQueryUseCase {
    QueryChatMessagesResult getMessages(ConversationId conversationId);
}
