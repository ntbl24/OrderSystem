package com.abug.profile.application.port.in;

import com.abug.profile.domain.Profile;

public interface UpdateAvatarUseCase {
    Profile updateAvatar(String avatar);
}
