package com.abug.chat_service.infrastructure.persistence.adapter;

import com.abug.chat_service.application.port.out.ConversationRepository;
import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.ConversationId;
import com.abug.chat_service.domain.UserId;
import com.abug.chat_service.infrastructure.persistence.mapper.ConversationMapper;
import com.abug.chat_service.infrastructure.persistence.repository.ConversationMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConversationRepoAdapter implements ConversationRepository {
    private final ConversationMongoRepository repository;

    @Override
    public Conversation save(Conversation conversation) {
        return ConversationMapper.domain(repository.save(ConversationMapper.document(conversation)));
    }

    @Override
    public Optional<Conversation> findByParticipantsHash(String hash) {
        return repository.findByParticipantsHash(hash)
                .map(ConversationMapper::domain);
    }

    @Override
    public List<Conversation> findAllByParticipantIdsContains(UserId userId) {
        return repository.findAllByParticipantsUserIdValue(userId.value()).stream()
                .map(ConversationMapper::domain)
                .toList();
    }

    @Override
    public Optional<Conversation> findById(ConversationId id) {
        return repository.findById(id.value())
                .map(ConversationMapper::domain);
    }
}
