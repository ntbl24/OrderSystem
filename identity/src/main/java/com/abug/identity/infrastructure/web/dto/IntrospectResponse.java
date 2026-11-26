package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.dto.IntrospectResult;

public record IntrospectResponse(
        boolean valid,
        String userId
) {

    public static IntrospectResponse from (IntrospectResult result){
        return new IntrospectResponse(result.valid(), result.userId().toString());
    }
}
