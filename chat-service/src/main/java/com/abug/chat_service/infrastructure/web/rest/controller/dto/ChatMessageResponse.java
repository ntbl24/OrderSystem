package com.abug.chat_service.infrastructure.web.rest.controller.dto;

import com.abug.chat_service.application.dto.CreateChatMessageResult;
import com.abug.chat_service.application.dto.QueryChatMessagesResult;
import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.valueobject.UserId;

import java.time.Instant;
import java.util.List;

public record ChatMessageResponse (
        String id,
        String conversationId,
        boolean me,
        String message,
        ParticipantInfoResponse sender,
        Instant createdDate
){
    public static ChatMessageResponse fromCreateResult(CreateChatMessageResult result) {
        return from(result.currentUser(), result.chatMessage());
    }

    public static List<ChatMessageResponse> fromQueryResult(QueryChatMessagesResult result) {
        return result.chatMessages().stream()
                .map(chatMessage -> from(result.currentUser(), chatMessage))
                .toList();
    }

    private static ChatMessageResponse from(UserId currentUser, ChatMessage chatMessage) {

        return new ChatMessageResponse(
                chatMessage.getId().toString(),
                chatMessage.getConversationId().toString(),
                currentUser.equals(chatMessage.getSender().getUserId()),
                chatMessage.getMessage(),
                ParticipantInfoResponse.from(chatMessage.getSender()),
                chatMessage.getCreatedDate());
    }
}
