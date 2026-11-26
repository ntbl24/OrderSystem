package com.abug.chat_service.infrastructure.web.rest.controller;

import com.abug.chat_service.application.port.in.ChatMessageQueryUseCase;
import com.abug.chat_service.application.port.in.CreateChatMessageUseCase;
import com.abug.chat_service.application.port.in.command.CreateChatMessageCommand;
import com.abug.chat_service.common.response.ApiResponse;
import com.abug.chat_service.domain.valueobject.ConversationId;
import com.abug.chat_service.infrastructure.web.rest.controller.dto.ChatMessageRequest;
import com.abug.chat_service.infrastructure.web.rest.controller.dto.ChatMessageResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class ChatMessageController {
    private final CreateChatMessageUseCase createChatMessageUseCase;
    private final ChatMessageQueryUseCase chatMessageQueryUseCase;

    @PostMapping("/create")
    ApiResponse<ChatMessageResponse> create(@RequestBody @Valid ChatMessageRequest request) {
        var command = new CreateChatMessageCommand(
                ConversationId.of(request.conversationId()),
                request.message());
        return ApiResponse.success(ChatMessageResponse.fromCreateResult(createChatMessageUseCase.create(command)));
    }

    @GetMapping
    ApiResponse<List<ChatMessageResponse>> getMessages(
            @RequestParam("conversationId") String conversationId) {
        return ApiResponse.success(ChatMessageResponse.fromQueryResult(chatMessageQueryUseCase.getMessages(ConversationId.of(conversationId))));
    }
}
