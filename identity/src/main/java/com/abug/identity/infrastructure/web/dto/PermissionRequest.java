package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.port.in.PermissionCommand;
import jakarta.validation.constraints.NotBlank;


public record PermissionRequest(
    @NotBlank
    String name,
    String description
) {


    public PermissionCommand toCommand(){
        return new PermissionCommand(name, description);
    }
}
