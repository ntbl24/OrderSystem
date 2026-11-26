package com.abug.chat_service.infrastructure.web.rest.controller.dto;

import com.abug.chat_service.application.port.in.command.CreateConversationCommand;
import com.abug.chat_service.domain.ConversationType;
import com.abug.chat_service.domain.valueobject.UserId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record ConversationRequest(
        ConversationType type,

        @Size(min = 1)
        @NotNull
        List<String> participantIds
) {
        public CreateConversationCommand toCreateCommand(){
                return new CreateConversationCommand(type, participantIds.stream().map(UserId::of).toList());
        }
}
