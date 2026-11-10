package com.abug.gateway.client;

import com.abug.gateway.common.response.ApiResponse;
import com.abug.gateway.dto.IntrospectRequest;
import com.abug.gateway.dto.IntrospectResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;
import reactor.core.publisher.Mono;

public interface IdentityClient {
    @PostExchange(url = "/auth/introspect", contentType = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<IntrospectResponse>> introspect(@RequestBody  IntrospectRequest request);
}
