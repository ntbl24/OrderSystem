package com.abug.gateway.service;

import com.abug.gateway.client.IdentityClient;
import com.abug.gateway.common.response.ApiResponse;
import com.abug.gateway.dto.IntrospectRequest;
import com.abug.gateway.dto.IntrospectResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class IdentityService {
    private final IdentityClient identityClient;
    public Mono<ApiResponse<IntrospectResponse>> introspect(String token){
        return identityClient.introspect(IntrospectRequest.builder()
                .token(token).build());
    }
}
