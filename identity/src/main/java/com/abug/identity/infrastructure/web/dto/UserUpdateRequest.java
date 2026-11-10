package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.port.in.CreateUserCommand;
import com.abug.identity.application.port.in.UpdateUserCommand;

import java.util.List;

public record UserUpdateRequest(
        String password,
        List<String>roles
) {
    public UpdateUserCommand toCommand(){
        return new UpdateUserCommand(password, roles);
    }
}
