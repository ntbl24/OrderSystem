package com.abug.chat_service.infrastructure.persistence.mapper;

import com.abug.chat_service.domain.Conversation;
import com.abug.chat_service.domain.valueobject.ConversationId;
import com.abug.chat_service.infrastructure.persistence.document.ConversationDocument;

public class ConversationMapper {
    public static Conversation domain(ConversationDocument document){
        return Conversation.reconstitute(
                ConversationId.of(document.getId()),
                document.getType(),
                document.getParticipantsHash(),
                document.getParticipants(),
                document.getCreatedDate(),
                document.getModifiedDate());
    }

    public static ConversationDocument document(Conversation document){
        return new ConversationDocument(
                document.getId().value(),
                document.getType(),
                document.getParticipantsHash(),
                document.getParticipants());
    }
}
