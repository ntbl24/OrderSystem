package com.abug.chat_service.application.port.out;

import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.valueobject.ConversationId;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository {
    Conversation save(Conversation conversation);

    Optional<Conversation> findByParticipantsHash(String hash);

    List<Conversation> findAllByParticipantIdsContains(UserId userId);

    Optional<Conversation> findById(ConversationId id);
}
