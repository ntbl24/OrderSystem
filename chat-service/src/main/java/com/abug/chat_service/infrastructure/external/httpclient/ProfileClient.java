package com.abug.chat_service.infrastructure.external.httpclient;

import com.abug.chat_service.common.response.ApiResponse;
import com.abug.chat_service.infrastructure.external.httpclient.dto.ProfileUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "profile-service", url = "${app.services.profile.url}")
public interface ProfileClient {
    @GetMapping("/internal/user/{userId}")
    ApiResponse<ProfileUserResponse> getProfile(@PathVariable String userId);
}
