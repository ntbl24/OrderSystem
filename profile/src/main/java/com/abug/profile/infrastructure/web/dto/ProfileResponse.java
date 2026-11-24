package com.abug.profile.infrastructure.web.dto;

import com.abug.profile.domain.Profile;

import java.time.LocalDate;

public record ProfileResponse(
        String id,
        String userId,
        String avatar,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate dob,
        String city) {
    public static ProfileResponse from(Profile profile) {
        return new ProfileResponse(
                profile.getId().toString(),
                profile.getUserId().toString(),
                profile.getAvatar(),
                profile.getUsername(),
                profile.getEmail(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getDob(),
                profile.getCity());
    }
}
