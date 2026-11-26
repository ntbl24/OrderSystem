package com.abug.chat_service.application.usecase;

import com.abug.chat_service.application.dto.CreateChatMessageResult;
import com.abug.chat_service.application.port.in.CreateChatMessageUseCase;
import com.abug.chat_service.application.port.in.command.CreateChatMessageCommand;
import com.abug.chat_service.application.port.out.ChatMessageRepository;
import com.abug.chat_service.application.port.out.ConversationRepository;
import com.abug.chat_service.application.port.out.CurrentUserPort;
import com.abug.chat_service.application.port.out.ProfileUserPort;
import com.abug.chat_service.common.exception.AppException;
import com.abug.chat_service.common.exception.ErrorCode;
import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.ParticipantInfo;
import com.abug.chat_service.domain.valueobject.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.List;


@Service
@RequiredArgsConstructor
public class CreateChatMessageService implements CreateChatMessageUseCase {
    private final CurrentUserPort currentUserPort;
    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ProfileUserPort profileUserPort;
    private final SendMessageService sendMessageService;
    private final ObjectMapper objectMapper;

    @Override
    public CreateChatMessageResult create(CreateChatMessageCommand command) {
        UserId userId = UserId.of(currentUserPort.currentUserId());
        Conversation conversation = conversationRepository.findById(command.conversationId())
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
        conversation.getParticipants().stream()
                .filter(participantInfo -> userId.equals(participantInfo.getUserId()))
                .findAny()
                .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

        ProfileUserPort.Snapshot userProfile = profileUserPort.getProfile(userId);

        ParticipantInfo userInfo = ParticipantInfo.create(userId, userProfile.username(), userProfile.firstName(), userProfile.lastName(), userProfile.avatar());

        ChatMessage chatMessage = ChatMessage.create(command.conversationId(), command.message(), userInfo);
        chatMessage = chatMessageRepository.save(chatMessage);
        String message = objectMapper.writeValueAsString(chatMessage);

        List<UserId> userIds = conversation.getParticipants().stream().map(ParticipantInfo::getUserId).toList();

        sendMessageService.sendMessageToUserId(userIds, message);
        return new CreateChatMessageResult(userId, chatMessage);
    }
}
