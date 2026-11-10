package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.domain.Role;

import java.util.Set;
import java.util.stream.Collectors;

public record RoleResponse(
    String name,
    String description,
    Set<PermissionResponse> permissions
) {
    public static RoleResponse from(Role role){
        return new RoleResponse(role.getName(), role.getDescription(), role.getPermissions().stream().map(PermissionResponse::from).collect(Collectors.toSet()));
    }
}
