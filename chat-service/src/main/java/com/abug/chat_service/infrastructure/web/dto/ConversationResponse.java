package com.abug.chat_service.infrastructure.web.dto;

import com.abug.chat_service.infrastructure.persistence.document.ParticipantDocument;

import java.time.Instant;
import java.util.List;

public record ConversationResponse(
        String id,
        String type,
        String participantsHash,
        String conversationAvatar,
        String conversationName,
        List<ParticipantDocument> participants,
        Instant createdDate,
        Instant modifiedDate
) {
}
