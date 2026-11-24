package com.abug.identity.infrastructure.persistence.adpater;

import com.abug.identity.application.port.out.RoleRepository;
import com.abug.identity.domain.Role;
import com.abug.identity.infrastructure.persistence.IdentityPersistenceMapper;
import com.abug.identity.infrastructure.persistence.repository.RoleJpaRepository;
import com.abug.identity.infrastructure.persistence.entity.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaRoleAdapter implements RoleRepository {
    private final RoleJpaRepository repository;

    @Override
    public Role save(Role role) {
        RoleEntity entity = IdentityPersistenceMapper.entity(role);
        return IdentityPersistenceMapper.domain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAll() {
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
    public Optional<Role> findById(String id) {
        return repository.findById(id).map(IdentityPersistenceMapper::domain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> findAllById(List<String> roles) {
        return repository.findAllById(roles).stream()
                .map(IdentityPersistenceMapper::domain)
                .toList();
    }
}
