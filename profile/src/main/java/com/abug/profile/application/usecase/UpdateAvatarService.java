package com.abug.profile.application.usecase;

import com.abug.profile.application.port.in.UpdateAvatarUseCase;
import com.abug.profile.application.port.out.CurrentUserPort;
import com.abug.profile.application.port.out.MediaUploadPort;
import com.abug.profile.application.port.out.ProfileRepository;
import com.abug.profile.common.exception.AppException;
import com.abug.profile.common.exception.ErrorCode;
import com.abug.profile.domain.Profile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateAvatarService implements UpdateAvatarUseCase {
    private final ProfileRepository profileRepository;
    private final CurrentUserPort currentUserPort;
    private final MediaUploadPort mediaUploadPort;

    @Override
    public Profile updateAvatar(MultipartFile file) {
        Profile profile = profileRepository.findByUserId(currentUserPort.currentUserId())
                .orElseThrow(() -> new AppException(ErrorCode.PROFILE_NOT_FOUND));
        MediaUploadPort.Snapshot uploadResult = mediaUploadPort.uploadMedia(file);
        profile.updateAvatar(uploadResult.url());
        return profileRepository.save(profile);
    }
}
