package com.abug.identity.application.dto;

import com.abug.identity.domain.UserId;

public record IntrospectResult(
        boolean valid,
        UserId userId
) {
}
