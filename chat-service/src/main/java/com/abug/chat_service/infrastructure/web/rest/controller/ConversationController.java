package com.abug.chat_service.infrastructure.web.rest.controller;

import com.abug.chat_service.application.port.in.ConversationQueryUseCase;
import com.abug.chat_service.application.port.in.CreateConversationUseCase;
import com.abug.chat_service.common.response.ApiResponse;
import com.abug.chat_service.infrastructure.web.rest.controller.dto.ConversationRequest;
import com.abug.chat_service.infrastructure.web.rest.controller.dto.ConversationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {
    private final CreateConversationUseCase createConversationUseCase;
    private final ConversationQueryUseCase conversationQueryUseCase;

    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody @Valid ConversationRequest request){
        return ApiResponse.success(ConversationResponse.fromCreateResult(createConversationUseCase.create(request.toCreateCommand())));
    }

    @GetMapping("/my-conversation")
    ApiResponse<List<ConversationResponse>> getConversations(){
        return ApiResponse.success(ConversationResponse.fromQueryCollectionResult(conversationQueryUseCase.myConversations()));
    }
}
