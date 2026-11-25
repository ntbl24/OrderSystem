package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.dto.QueryConversationResult;
import com.abug.chat_service.domain.Conversation;

import java.util.List;

public interface ConversationQueryUseCase {
    QueryConversationResult myConversations();
}
