package com.abug.identity.application.port.in;

import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;

public record IntrospectCommand(String token) {
    public IntrospectCommand{
        if(token == null)
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Token is required");
    }
}
