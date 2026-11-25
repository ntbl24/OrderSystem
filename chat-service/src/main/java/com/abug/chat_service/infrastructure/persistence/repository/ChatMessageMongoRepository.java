package com.abug.chat_service.infrastructure.persistence.repository;

import com.abug.chat_service.infrastructure.persistence.document.ChatMessageDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.UUID;

public interface ChatMessageMongoRepository extends MongoRepository<ChatMessageDocument, UUID> {
    List<ChatMessageDocument> findAllByConversationIdOrderByCreatedDateDesc(UUID conversationId);
}
