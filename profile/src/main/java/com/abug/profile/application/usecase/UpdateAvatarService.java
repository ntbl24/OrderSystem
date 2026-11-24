package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.UpdateAvatarUseCase;
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
public class UpdateAvatarService implements UpdateAvatarUseCase {
    private final ProfileRepository profileRepository;
    private final CurrentUserPort currentUserPort;

    @Override
    public Profile updateAvatar(String avatar) {
        Profile profile = profileRepository.findByUserId(currentUserPort.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        profile.updateAvatar(avatar);
        return profileRepository.save(profile);
    }
}
