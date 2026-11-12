package com.abug.identity.infrastructure.persistence;

import com.abug.identity.application.port.out.InvalidatedTokenRepository;
import com.abug.identity.domain.InvalidatedToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaInvalidatedTokenAdapter implements InvalidatedTokenRepository {
    private final InvalidatedTokenJpaRepository repository;
    @Override
    public boolean existsById(String id) {
        return repository.existsById(id);
    }

    @Override
    public InvalidatedToken save(InvalidatedToken invalidatedToken) {
        return IdentityPersistenceMapper.domain(repository.save(IdentityPersistenceMapper.entity(invalidatedToken)));
    }
}
