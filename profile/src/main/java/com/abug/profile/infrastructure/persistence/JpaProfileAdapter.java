package com.abug.profile.infrastructure.persistence;

import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import com.abug.profile.domain.Profile;
import com.abug.profile.infrastructure.persistence.repository.ProfileJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class JpaProfileAdapter implements ProfileRepository {
    private final ProfileJpaRepository repository;

    @Override
    public Profile save(Profile profile) {
        try {
            return ProfilePersistenceMapper.domain(repository.saveAndFlush(ProfilePersistenceMapper.entity(profile)));
        } catch (DataIntegrityViolationException exception) {
            throw new AppException(ErrorCode.PROFILE_ALREADY_EXISTS, exception);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findById(UUID profileId) {
        return repository.findById(profileId).map(ProfilePersistenceMapper::domain);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> findByUserId(UUID userId) {
        return repository.findByUserId(userId).map(ProfilePersistenceMapper::domain);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Profile> findAll(Pageable pageable) {
        return repository.findAll(pageable).map(ProfilePersistenceMapper::domain);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Profile> search(String query, Pageable pageable) {
        return repository.search(query, pageable).map(ProfilePersistenceMapper::domain);
    }
}
