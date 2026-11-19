package com.abug.identity.application.usecase;

import com.abug.identity.application.port.in.PermissionCommand;
import com.abug.identity.application.port.in.PermissionUseCase;
import com.abug.identity.application.port.out.PermissionRepository;
import com.abug.identity.domain.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService implements PermissionUseCase {
    private final PermissionRepository permissionRepository;

    @Transactional
    public Permission create(PermissionCommand command) {
        Permission permission = Permission.create(command.name(), command.description());
        permission = permissionRepository.save(permission);
        return permission;
    }

    public List<Permission> getAll() {
        return permissionRepository.findAll();
    }

    @Transactional
    public void delete(String permission) {
        permissionRepository.deleteById(permission);
    }
}
