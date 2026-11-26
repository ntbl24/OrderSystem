package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.port.in.command.CreateWebSocketSessionCommand;
import com.abug.chat_service.domain.WebSocketSession;

public interface CreateSessionUseCase {
    WebSocketSession create(CreateWebSocketSessionCommand command);
}
