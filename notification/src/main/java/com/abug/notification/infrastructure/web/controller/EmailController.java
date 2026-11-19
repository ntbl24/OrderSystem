package com.abug.notification.infrastructure.web.controller;

import com.abug.notification.application.port.in.EmailUseCase;
import com.abug.notification.common.response.ApiResponse;
import com.abug.notification.infrastructure.web.dto.SendEmailRequest;
import com.abug.notification.infrastructure.web.dto.SendEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailUseCase emailUseCase;

    @PostMapping("/email/send")
    ApiResponse<SendEmailResponse> sendEmail(@RequestBody SendEmailRequest request){
        return ApiResponse.success(SendEmailResponse.from(emailUseCase.sendEmail(request.toCommand())));
    }


}
