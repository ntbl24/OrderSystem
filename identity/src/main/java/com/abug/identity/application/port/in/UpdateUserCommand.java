package com.abug.identity.application.port.in;

import com.abug.identity.common.exception.AppException;
import com.abug.identity.common.exception.ErrorCode;

import java.util.List;

public record UpdateUserCommand(
        String password,
        List<String> roles
) {
    public UpdateUserCommand{
        if(password == null || password.isBlank()){
            throw new AppException(ErrorCode.INVALID_INPUT, "Password must be not null or blank");
        }
    }
}
