package com.abug.profile.application.port.out;

import com.abug.profile.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ProfileRepository {
    Profile save(Profile profile);

    Optional<Profile> findById(UUID profileId);

    Optional<Profile> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    Page<Profile> findAll(Pageable pageable);

    Page<Profile> search(String query, Pageable pageable);
}
