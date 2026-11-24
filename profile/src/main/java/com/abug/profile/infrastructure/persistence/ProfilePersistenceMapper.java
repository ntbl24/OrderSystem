package com.abug.profile.infrastructure.persistence;

import com.abug.profile.domain.Profile;
import com.abug.profile.infrastructure.persistence.entity.ProfileEntity;

public final class ProfilePersistenceMapper {
    private ProfilePersistenceMapper() {
    }

    public static Profile domain(ProfileEntity entity) {
        return Profile.reconstitute(
                entity.getId(),
                entity.getUserId(),
                entity.getAvatar(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getDob(),
                entity.getCity(),
                entity.getVersion());
    }

    public static ProfileEntity entity(Profile profile) {
        return new ProfileEntity(
                profile.getId(),
                profile.getUserId(),
                profile.getAvatar(),
                profile.getUsername(),
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDob(),
                profile.getCity(),
                profile.getVersion());
    }
}
