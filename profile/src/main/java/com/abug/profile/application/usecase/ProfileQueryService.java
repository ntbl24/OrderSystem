package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.ProfileQueryUseCase;
import com.abug.profile.application.port.out.CurrentUserPort;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import com.abug.profile.domain.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileQueryService implements ProfileQueryUseCase {
    private final ProfileRepository profileRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    public Profile getProfile(UUID profileId) {
        return profileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
    }

    @Override
    public Page<Profile> getAllProfiles(Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    @Override
    public Profile getMyProfile() {
        return getProfileByUserId(currentUserPort.currentUserId());
    }

    @Override
    public Page<Profile> search(String query, Pageable pageable) {
        return profileRepository.search(query, pageable);
    }

    @Override
    public Profile getProfileByUserId(UUID userId) {
        return profileRepository.findByUserId(userId)
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
    }
}
