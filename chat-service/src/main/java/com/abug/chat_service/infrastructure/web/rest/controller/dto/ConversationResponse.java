package com.abug.chat_service.infrastructure.web.rest.controller.dto;

import com.abug.chat_service.application.dto.CreateConversationResult;
import com.abug.chat_service.application.dto.QueryConversationResult;
import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.ParticipantInfo;
import com.abug.chat_service.domain.UserId;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public record ConversationResponse(
        String id,
        String type,
        String participantsHash,
        String conversationAvatar,
        String conversationName,
        List<ParticipantInfoResponse> participants,
        Instant createdDate,
        Instant modifiedDate
) {
    public static ConversationResponse fromCreateResult(CreateConversationResult result){
        return toConversationResponse(result.currentUser(), result.conversation());
    }

    public static List<ConversationResponse> fromQueryCollectionResult(QueryConversationResult result){
        return result.conversations().stream().map(conversation -> toConversationResponse(result.currentUser(), conversation)).toList();
    }

    public static ConversationResponse toConversationResponse(UserId currentUser, Conversation conversation){
        Optional<ParticipantInfo> firstParticipant = conversation.getParticipants().stream()
                .filter(participantInfo -> !participantInfo.getUserId().equals(currentUser))
                .findFirst();

        return new ConversationResponse(
                conversation.getId().toString(),
                conversation.getType().toString(),
                conversation.getParticipantsHash(),
                firstParticipant.map(ParticipantInfo::getAvatar).orElse(null),
                firstParticipant.map(ParticipantInfo::getUsername).orElse(null),
                conversation.getParticipants().stream().map(ParticipantInfoResponse::from).toList(),
                conversation.getCreatedDate(),
                conversation.getModifiedDate());
    }


}
