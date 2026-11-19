package com.abug.identity.application.usecase;

import com.abug.identity.application.port.in.RoleCommand;
import com.abug.identity.application.port.in.RoleUseCase;
import com.abug.identity.application.port.out.PermissionRepository;
import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.domain.Permission;
import com.abug.identity.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RoleService implements RoleUseCase {
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;

    @Override
    @Transactional
    public Role create(RoleCommand command) {
        Set<Permission> permissions = permissionRepository.findAllById(command.permissions());
        return Role.create(command.name(), command.description(), permissions);
    }

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional
    public void delete(String role) {
        roleRepository.deleteById(role);
    }
}
