package com.abug.chat_service.infrastructure.persistence.document;

import com.abug.chat_service.domain.ParticipantInfo;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Document(collection = "conversation")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDocument {
    @MongoId
    UUID id;
    String type;
    @Indexed(unique = true)
    String participantsHash;
    List<ParticipantInfo> participants;
    @CreatedDate
    Instant createdDate;
    @LastModifiedDate
    Instant modifiedDate;
}
