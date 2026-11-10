package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.domain.Permission;

public record PermissionResponse(
    String name,
    String description
) {
    public static PermissionResponse from(Permission permission){
        return new PermissionResponse(permission.getName(), permission.getDescription());
    }
}
