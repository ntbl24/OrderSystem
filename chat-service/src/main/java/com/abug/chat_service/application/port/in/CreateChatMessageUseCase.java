package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.dto.CreateChatMessageResult;
import com.abug.chat_service.application.port.in.command.CreateChatMessageCommand;

public interface CreateChatMessageUseCase {
    CreateChatMessageResult create(CreateChatMessageCommand command);
}
