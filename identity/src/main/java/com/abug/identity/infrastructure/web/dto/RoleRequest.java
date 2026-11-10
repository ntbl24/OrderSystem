package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.port.in.RoleCommand;

import java.util.Set;

public record RoleRequest(
    String name,
    String description,
    Set<String> permissions
) {
    public RoleCommand toCommand(){
        return new RoleCommand(name, description, permissions);
    }
}
