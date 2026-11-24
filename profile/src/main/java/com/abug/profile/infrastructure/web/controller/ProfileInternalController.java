package com.abug.profile.infrastructure.web.controller;

import com.abug.profile.application.port.in.CreateProfileCommand;
import com.abug.profile.application.port.in.CreateProfileUseCase;
import com.abug.profile.application.port.in.ProfileQueryUseCase;
import com.abug.profile.common.response.ApiResponse;
import com.abug.profile.infrastructure.web.dto.CreateProfileRequest;
import com.abug.profile.infrastructure.web.dto.ProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/profiles")
@RequiredArgsConstructor
public class ProfileInternalController {
    private final CreateProfileUseCase createProfileUseCase;
    private final ProfileQueryUseCase profileQueryUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProfileResponse> createProfile(@Valid @RequestBody CreateProfileRequest request) {
        return ApiResponse.success(ProfileResponse.from(
                createProfileUseCase.createProfile(
                        new CreateProfileCommand(request.userId(), request.username(), request.email()))));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<ProfileResponse> getProfileByUserId(@PathVariable UUID userId) {
        return ApiResponse.success(ProfileResponse.from(profileQueryUseCase.getProfileByUserId(userId)));
    }
}
