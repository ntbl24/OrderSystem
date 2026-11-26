package com.abug.chat_service.infrastructure.external.httpclient.dto;

public record IntrospectResponse(
        boolean valid,
        String userId
) {
}
