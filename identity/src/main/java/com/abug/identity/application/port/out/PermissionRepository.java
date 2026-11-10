package com.abug.identity.application.port.out;

import com.abug.identity.domain.Permission;

import java.util.List;
import java.util.Set;

public interface PermissionRepository {
    Permission save(Permission permission);
    List<Permission> findAll();
    void deleteById(String id);
    Set<Permission> findAllById(Set<String> ids);
}
