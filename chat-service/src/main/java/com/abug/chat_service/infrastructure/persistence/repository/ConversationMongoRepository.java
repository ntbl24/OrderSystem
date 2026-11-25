package com.abug.chat_service.infrastructure.persistence.repository;

import com.abug.chat_service.infrastructure.persistence.document.ConversationDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationMongoRepository extends MongoRepository<ConversationDocument, UUID> {
    Optional<ConversationDocument> findByParticipantsHash(String participantsHash);

    List<ConversationDocument> findAllByParticipantsUserIdValue(UUID userId);
}
