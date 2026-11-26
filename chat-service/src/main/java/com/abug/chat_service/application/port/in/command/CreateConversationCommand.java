package com.abug.chat_service.application.port.in.command;

import com.abug.chat_service.domain.ConversationType;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;

public record CreateConversationCommand(
    ConversationType type,
    List<UserId> participantIds
) {
}
