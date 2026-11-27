package com.abug.identity.infrastructure.persistence;

import com.abug.identity.domain.*;
import com.abug.identity.infrastructure.persistence.entity.*;

import java.util.Set;
import java.util.stream.Collectors;

public class IdentityPersistenceMapper {
    private IdentityPersistenceMapper() {
    }

    public static Permission domain(PermissionEntity entity) {
        return Permission.create(entity.getName(), entity.getDescription());
    }

    public static PermissionEntity entity(Permission permission) {
        return new PermissionEntity(permission.getName(), permission.getDescription());
    }

    public static Role domain(RoleEntity entity) {
        Set<Permission> permissions = entity.getPermissions() == null
                ? Set.of()
                : entity.getPermissions().stream()
                        .map(IdentityPersistenceMapper::domain)
                        .collect(Collectors.toSet());
        return Role.create(entity.getName(), entity.getDescription(), permissions);
    }

    public static RoleEntity entity(Role role) {
        Set<PermissionEntity> permissions = role.getPermissions() == null
                ? Set.of()
                : role.getPermissions().stream()
                        .map(IdentityPersistenceMapper::entity)
                        .collect(Collectors.toSet());
        return new RoleEntity(role.getName(), role.getDescription(), permissions);
    }

    public static User domain(UserEntity entity) {
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

    public static UserEntity entity(User user) {
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

    public static InvalidatedTokenEntity entity(InvalidatedToken invalidatedToken){
        return new InvalidatedTokenEntity(
                invalidatedToken.getId(),
                invalidatedToken.getExpiryTime()
        );
    }

    public static InvalidatedToken domain(InvalidatedTokenEntity entity){
        return InvalidatedToken.create(entity.getId(), entity.getExpiryTime());
    }

    public static OutboxEventEntity entity(OutboxEvent outboxEvent){
        return OutboxEventEntity.create(outboxEvent.getId().value(), outboxEvent.getAggregateId(), outboxEvent.getAggregateType(), outboxEvent.getEventType(), outboxEvent.getTopic(), outboxEvent.getPayload(), outboxEvent.isPublished());
    }

    public static OutboxEvent domain(OutboxEventEntity entity){
        return OutboxEvent.create(EventId.of(entity.getId()), entity.getAggregateId(), entity.getAggregateType(), entity.getEventType(), entity.getTopic(), entity.getPayload(), entity.isPublished());
    }
}
