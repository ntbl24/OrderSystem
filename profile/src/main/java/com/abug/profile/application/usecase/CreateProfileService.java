package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.CreateProfileCommand;
import com.abug.profile.application.port.in.CreateProfileUseCase;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import com.abug.profile.domain.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateProfileService implements CreateProfileUseCase {
    private final ProfileRepository profileRepository;

    @Override
    public Profile createProfile(CreateProfileCommand command) {
        if (profileRepository.existsByUserId(command.userId())) {
            throw new AppException(ErrorCode.PROFILE_ALREADY_EXISTS);
        }
        return profileRepository.save(Profile.create(command.userId(), command.username(), command.email()));
    }
}
