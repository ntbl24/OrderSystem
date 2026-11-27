package com.abug.chat_service.application.port.in;

import com.abug.chat_service.application.port.in.command.SendMessageCommand;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;

public interface SendMessageUseCase {
    void sendMessageToUserId(SendMessageCommand command);
}
