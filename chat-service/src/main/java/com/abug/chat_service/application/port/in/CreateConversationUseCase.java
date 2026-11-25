package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.dto.CreateConversationResult;
import com.abug.chat_service.application.port.in.command.CreateConversationCommand;
import com.abug.chat_service.domain.Conversation;

public interface CreateConversationUseCase {
    CreateConversationResult create(CreateConversationCommand command);
}
