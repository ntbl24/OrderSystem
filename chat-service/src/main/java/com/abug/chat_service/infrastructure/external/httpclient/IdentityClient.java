package com.abug.chat_service.infrastructure.external.httpclient;

import com.abug.chat_service.common.response.ApiResponse;
import com.abug.chat_service.infrastructure.external.httpclient.dto.IntrospectRequest;
import com.abug.chat_service.infrastructure.external.httpclient.dto.IntrospectResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "identity-service", url = "${app.services.identity.url}")
public interface IdentityClient {
    @PostMapping("/auth/introspect")
    ApiResponse<IntrospectResponse> introspect(@RequestBody IntrospectRequest request);
}