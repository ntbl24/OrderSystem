package com.abug.chat_service.infrastructure.persistence.adapter;

import com.abug.chat_service.application.port.out.ChatMessageRepository;
import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.ConversationId;
import com.abug.chat_service.infrastructure.persistence.mapper.ChatMessageMapper;
import com.abug.chat_service.infrastructure.persistence.repository.ChatMessageMongoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMessageRepoAdapter implements ChatMessageRepository {
    private final ChatMessageMongoRepository repository;

    @Override
    public ChatMessage save(ChatMessage chatMessage) {
        return ChatMessageMapper.domain(repository.save(ChatMessageMapper.document(chatMessage)));
    }

    @Override
    public List<ChatMessage> findAllByConversationIdOrderByCreatedDateDesc(ConversationId conversationId) {
        return repository.findAllByConversationIdOrderByCreatedDateDesc(conversationId.value()).stream()
                .map(ChatMessageMapper::domain)
                .toList();
    }
}
