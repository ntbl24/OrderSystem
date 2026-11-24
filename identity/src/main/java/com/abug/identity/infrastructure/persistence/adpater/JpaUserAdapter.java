package com.abug.identity.infrastructure.persistence.adpater;

import com.abug.identity.application.port.out.UserRepository;
import com.abug.identity.domain.User;
import com.abug.identity.domain.UserId;
import com.abug.identity.infrastructure.persistence.IdentityPersistenceMapper;
import com.abug.identity.infrastructure.persistence.repository.UserJpaRepository;
import com.abug.identity.infrastructure.persistence.entity.UserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaUserAdapter implements UserRepository {
    private final UserJpaRepository repository;

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return repository.findByUserName(username).map(IdentityPersistenceMapper::domain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return repository.existsByUserName(username);
    }

    @Override
    public User save(User user) {
        UserEntity entity = IdentityPersistenceMapper.entity(user);
        return IdentityPersistenceMapper.domain(repository.saveAndFlush(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(UserId userId) {
        return repository.findById(userId.value()).map(IdentityPersistenceMapper::domain);
    }

    @Override
    public void deleteById(UserId userId) {
        repository.deleteById(userId.value());
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {
        return repository.findAll().stream()
                .map(IdentityPersistenceMapper::domain)
                .toList();
    }
}
