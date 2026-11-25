package com.abug.chat_service.domain;

import java.time.Instant;

public class ChatMessage {
    private MessageId id;
    private ConversationId conversationId;
    private String message;
    private ParticipantInfo sender;
    private Instant createdDate;

    private ChatMessage(MessageId id, ConversationId conversationId, String message, ParticipantInfo sender, Instant createdDate) {
        this.id = id;
        this.conversationId = conversationId;
        this.message = message;
        this.sender = sender;
        this.createdDate = createdDate;
    }

    public static ChatMessage create(ConversationId conversationId, String message, ParticipantInfo sender){
        return new ChatMessage(MessageId.generate(), conversationId, message, sender, Instant.now());
    }

    public static ChatMessage reconstitute(MessageId id, ConversationId conversationId, String message, ParticipantInfo sender, Instant createdDate){
        return new ChatMessage(id, conversationId, message, sender, createdDate);
    }

    public MessageId getId() {
        return id;
    }

    public ConversationId getConversationId() {
        return conversationId;
    }

    public String getMessage() {
        return message;
    }

    public ParticipantInfo getSender() {
        return sender;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }
}
