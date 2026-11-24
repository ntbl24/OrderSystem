package com.abug.profile.application.port.in;

import com.abug.profile.domain.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProfileQueryUseCase {
    Profile getProfile(UUID profileId);

    Page<Profile> getAllProfiles(Pageable pageable);

    Profile getMyProfile();

    Page<Profile> search(String query, Pageable pageable);

    Profile getProfileByUserId(UUID userId);
}
