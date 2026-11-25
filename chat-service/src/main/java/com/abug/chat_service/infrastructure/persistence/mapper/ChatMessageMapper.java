package com.abug.chat_service.infrastructure.persistence.mapper;

import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.ConversationId;
import com.abug.chat_service.domain.MessageId;
import com.abug.chat_service.domain.ParticipantInfo;
import com.abug.chat_service.domain.UserId;
import com.abug.chat_service.infrastructure.persistence.document.ChatMessageDocument;
import com.abug.chat_service.infrastructure.persistence.document.ParticipantDocument;

public class ChatMessageMapper {
    private ChatMessageMapper() {
    }

    public static ChatMessage domain(ChatMessageDocument document) {
        return ChatMessage.reconstitute(
                MessageId.of(document.getId()),
                ConversationId.of(document.getConversationId()),
                document.getMessage(),
                domain(document.getSender()),
                document.getCreatedDate());
    }

    public static ChatMessageDocument document(ChatMessage chatMessage) {
        return new ChatMessageDocument(
                chatMessage.getId().value(),
                chatMessage.getConversationId().value(),
                chatMessage.getMessage(),
                document(chatMessage.getSender()),
                chatMessage.getCreatedDate());
    }

    private static ParticipantInfo domain(ParticipantDocument document) {
        return ParticipantInfo.create(
                UserId.of(document.getUserId()),
                document.getUsername(),
                document.getFirstName(),
                document.getLastName(),
                document.getAvatar());
    }

    private static ParticipantDocument document(ParticipantInfo participantInfo) {
        return new ParticipantDocument(
                participantInfo.getUserId().value(),
                participantInfo.getUsername(),
                participantInfo.getFirstName(),
                participantInfo.getLastName(),
                participantInfo.getAvatar());
    }
}
