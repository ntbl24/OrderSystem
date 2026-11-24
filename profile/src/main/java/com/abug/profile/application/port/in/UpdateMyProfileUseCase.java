package com.abug.profile.application.port.in;

import com.abug.profile.domain.Profile;

public interface UpdateMyProfileUseCase {
    Profile updateMyProfile(UpdateMyProfileCommand command);
}
