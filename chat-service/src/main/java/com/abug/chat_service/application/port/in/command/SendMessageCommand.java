package com.abug.chat_service.application.port.in.command;

import com.abug.chat_service.application.dto.Message;
import com.abug.chat_service.domain.ChatMessage;
import com.abug.chat_service.domain.valueobject.UserId;

import java.util.List;

public record SendMessageCommand(
        List<UserId> userIds, Message message
) {
}
