package com.abug.chat_service.application.dto;

import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.UserId;

import java.util.List;

public record QueryConversationResult(
        UserId currentUser,
        List<Conversation> conversations
) {
}
