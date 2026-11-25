package com.abug.chat_service.application.port.out;

import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.ConversationId;

import java.util.List;

public interface ChatMessageRepository {
    ChatMessage save(ChatMessage chatMessage);

    List<ChatMessage> findAllByConversationIdOrderByCreatedDateDesc(ConversationId conversationId);
}
