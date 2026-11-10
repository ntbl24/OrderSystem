package com.abug.identity.application.port.in;

import com.abug.identity.domain.Permission;

import java.util.List;

public interface PermissionUseCase {
    Permission create(PermissionCommand command);
    List<Permission> getAll();
    void delete(String permission);
}
