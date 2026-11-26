package com.abug.chat_service.application.port.in.command;

import com.abug.chat_service.domain.valueobject.UserId;

public record CreateWebSocketSessionCommand(
        String sessionId,
        UserId userId
) {
}
