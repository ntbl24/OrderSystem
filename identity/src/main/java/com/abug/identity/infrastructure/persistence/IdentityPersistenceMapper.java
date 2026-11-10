package com.abug.identity.infrastructure.persistence;

import com.abug.identity.domain.Permission;
import com.abug.identity.domain.Role;
import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;

import java.util.Set;
import java.util.stream.Collectors;

final class IdentityPersistenceMapper {
    private IdentityPersistenceMapper() {
    }

    static Permission domain(PermissionEntity entity) {
        return Permission.create(entity.getName(), entity.getDescription());
    }

    static PermissionEntity entity(Permission permission) {
        return new PermissionEntity(permission.getName(), permission.getDescription());
    }

    static Role domain(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions() == null
                ? Set.of()
                : entity.getPermissions().stream()
                        .map(IdentityPersistenceMapper::domain)
                        .collect(Collectors.toSet());
        return Role.create(entity.getName(), entity.getDescription(), permissions);
    }

    static RoleEntity entity(Role role) {
        Set<PermissionEntity> permissions = role.getPermissions() == null
                ? Set.of()
                : role.getPermissions().stream()
                        .map(IdentityPersistenceMapper::entity)
                        .collect(Collectors.toSet());
        return new RoleEntity(role.getName(), role.getDescription(), permissions);
    }

    static User domain(UserEntity entity) {
        Set<Role> roles = entity.getRoles() == null
                ? Set.of()
                : entity.getRoles().stream()
                        .map(IdentityPersistenceMapper::domain)
                        .collect(Collectors.toSet());
        return User.reconstitute(
                UserId.of(entity.getId()),
                entity.getEmail(),
                entity.getUserName(),
                entity.getPassword(),
                entity.isEmailVerified(),
                roles);
    }

    static UserEntity entity(User user) {
        Set<RoleEntity> roles = user.getRoles() == null
                ? Set.of()
                : user.getRoles().stream()
                        .map(IdentityPersistenceMapper::entity)
                        .collect(Collectors.toSet());
        return new UserEntity(
                user.getId().value(),
                user.getEmail(),
                user.getUserName(),
                user.getPassword(),
                user.isEmailVerified(),
                roles);
    }
}
