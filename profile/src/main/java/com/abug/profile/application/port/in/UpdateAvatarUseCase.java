package com.abug.profile.application.port.in;

import com.abug.profile.domain.Profile;
import org.springframework.web.multipart.MultipartFile;

public interface UpdateAvatarUseCase {
    Profile updateAvatar(MultipartFile file);
}
