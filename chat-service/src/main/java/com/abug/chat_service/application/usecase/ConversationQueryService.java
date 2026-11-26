package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.dto.QueryConversationResult;
import com.abug.chat_service.application.port.in.ConversationQueryUseCase;
import com.abug.chat_service.application.port.out.ConversationRepository;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ConversationQueryService implements ConversationQueryUseCase {
    private final CurrentUserPort currentUserPort;
    private final ConversationRepository conversationRepository;

    @Override
    public QueryConversationResult myConversations() {
        UUID userId = currentUserPort.currentUserId();

        List<Conversation> conversations= conversationRepository.findAllByParticipantIdsContains(UserId.of(userId));
        return new QueryConversationResult(UserId.of(userId), conversations);
    }
}
