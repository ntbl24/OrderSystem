package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.UpdateMyProfileCommand;
import com.abug.profile.application.port.in.UpdateMyProfileUseCase;
import com.abug.profile.application.port.out.CurrentUserPort;
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
public class UpdateMyProfileService implements UpdateMyProfileUseCase {
    private final ProfileRepository profileRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    public Profile updateMyProfile(UpdateMyProfileCommand command) {
        Profile profile = profileRepository.findByUserId(currentUserPort.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        profile.update(command.firstName(), command.lastName(), command.dob(), command.city());
        return profileRepository.save(profile);
    }
}
