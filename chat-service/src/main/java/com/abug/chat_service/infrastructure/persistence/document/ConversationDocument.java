package com.abug.chat_service.infrastructure.persistence.document;

import com.abug.chat_service.domain.ParticipantInfo;
import com.abug.chat_service.domain.ConversationType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
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
@Getter
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ConversationDocument {
    @MongoId
    UUID id;
    ConversationType type;
    @Indexed(unique = true)
    String participantsHash;
    List<ParticipantInfo> participants;
    @CreatedDate
    Instant createdDate;
    @LastModifiedDate
    Instant modifiedDate;

    public ConversationDocument(UUID id, ConversationType type, String participantsHash, List<ParticipantInfo> participants) {
        this.id = id;
        this.type = type;
        this.participantsHash = participantsHash;
        this.participants = participants;
    }
}
