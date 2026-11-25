package com.abug.chat_service.infrastructure.persistence.document;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.UUID;

@Document(collection = "chat_message")
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatMessageDocument {
    @MongoId
    UUID id;

    @Indexed
    UUID conversationId;

    String message;

    ParticipantDocument sender;

    @Indexed
    @CreatedDate
    Instant createdDate;
}
