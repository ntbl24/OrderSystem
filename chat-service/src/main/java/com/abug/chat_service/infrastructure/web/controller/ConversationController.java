package com.abug.chat_service.infrastructure.web.controller;

import com.abug.chat_service.common.response.ApiResponse;
import com.abug.chat_service.infrastructure.web.dto.ConversationRequest;
import com.abug.chat_service.infrastructure.web.dto.ConversationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/conversation")
@RequiredArgsConstructor
public class ConversationController {
    @PostMapping("/create")
    ApiResponse<ConversationResponse> createConversation(@RequestBody @Valid ConversationRequest request){
        return ApiResponse.success(ConversationResponse);
    }

    @GetMapping("/my-conversation")
    ApiResponse<List<ConversationResponse>> getConversations(){
        return ApiResponse.success(ConversationResponse)
    }
}
