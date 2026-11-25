package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.dto.QueryChatMessagesResult;
import com.abug.chat_service.application.port.in.ChatMessageQueryUseCase;
import com.abug.chat_service.application.port.out.ChatMessageRepository;
import com.abug.chat_service.application.port.out.ConversationRepository;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import com.abug.chat_service.application.port.out.ProfileUserPort;
import com.abug.chat_service.common.exception.AppException;
import com.abug.chat_service.common.exception.ErrorCode;
import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.ConversationId;
import com.abug.chat_service.domain.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class ChatMessageQueryService implements ChatMessageQueryUseCase{
    private final CurrentUserPort currentUserPort;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProfileUserPort profileUserPort;
    @Override
    public QueryChatMessagesResult getMessages(ConversationId conversationId) {
        UserId userId = UserId.of(currentUserPort.currentUserId());
        conversationRepository.findById(conversationId)
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND))
                .getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny().orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        List<ChatMessage> messages = chatMessageRepository.findAllByConversationIdOrderByCreatedDateDesc(conversationId);
        return new QueryChatMessagesResult(userId, messages);
    }
}
