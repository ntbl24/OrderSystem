package com.abug.identity.infrastructure.persistence.adpater;

import com.abug.identity.application.port.out.PermissionRepository;
import com.abug.identity.domain.Permission;
import com.abug.identity.infrastructure.persistence.IdentityPersistenceMapper;
import com.abug.identity.infrastructure.persistence.repository.PermissionJpaRepository;
import com.abug.identity.infrastructure.persistence.entity.PermissionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaPermissionAdapter implements PermissionRepository {
    private final PermissionJpaRepository repository;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = new PermissionEntity(permission.getName(), permission.getDescription());
        return IdentityPersistenceMapper.domain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Permission> findAll() {
        return repository.findAll().stream()
                .map(IdentityPersistenceMapper::domain)
                .toList();
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<Permission> findAllById(Set<String> ids) {
        return repository.findAllById(ids).stream()
                .map(IdentityPersistenceMapper::domain)
                .collect(Collectors.toSet());
    }
}
