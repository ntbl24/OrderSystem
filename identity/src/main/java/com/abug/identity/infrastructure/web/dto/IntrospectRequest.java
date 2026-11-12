package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.port.in.IntrospectCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record IntrospectRequest (
    @NotNull
    @NotBlank
    String token) {
    public IntrospectCommand toCommand(){
        return new IntrospectCommand(token);
    }
}
