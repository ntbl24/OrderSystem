package com.abug.profile.infrastructure.web.controller;

import com.abug.profile.application.port.in.ProfileQueryUseCase;
import com.abug.profile.application.port.in.UpdateAvatarUseCase;
import com.abug.profile.application.port.in.UpdateMyProfileCommand;
import com.abug.profile.application.port.in.UpdateMyProfileUseCase;
import com.abug.profile.common.response.ApiResponse;
import com.abug.profile.infrastructure.web.dto.PageResponse;
import com.abug.profile.infrastructure.web.dto.ProfileResponse;
import com.abug.profile.infrastructure.web.dto.UpdateAvatarRequest;
import com.abug.profile.infrastructure.web.dto.UpdateMyProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
public class ProfileController {
    private final ProfileQueryUseCase profileQueryUseCase;
    private final UpdateMyProfileUseCase updateMyProfileUseCase;
    private final UpdateAvatarUseCase updateAvatarUseCase;

    @GetMapping("/{profileId}")
    public ApiResponse<ProfileResponse> getProfile(@PathVariable UUID profileId) {
        return ApiResponse.success(ProfileResponse.from(profileQueryUseCase.getProfile(profileId)));
    }

    @GetMapping
    public ApiResponse<PageResponse<ProfileResponse>> getAllProfiles(@PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(profileQueryUseCase.getAllProfiles(pageable), ProfileResponse::from));
    }

    @GetMapping("/me")
    public ApiResponse<ProfileResponse> getMyProfile() {
        return ApiResponse.success(ProfileResponse.from(profileQueryUseCase.getMyProfile()));
    }

    @PutMapping("/me")
    public ApiResponse<ProfileResponse> updateMyProfile(@Valid @RequestBody UpdateMyProfileRequest request) {
        return ApiResponse.success(ProfileResponse.from(updateMyProfileUseCase.updateMyProfile(
                new UpdateMyProfileCommand(request.firstName(), request.lastName(), request.dob(), request.city()))));
    }

    @PutMapping("/me/avatar")
    public ApiResponse<ProfileResponse> updateAvatar(@RequestPart("file") MultipartFile file) {
        return ApiResponse.success(ProfileResponse.from(updateAvatarUseCase.updateAvatar(file)));
    }

    @GetMapping("/user/search")
    public ApiResponse<PageResponse<ProfileResponse>> search(
            @RequestParam @NotBlank String query,
            @PageableDefault(size = 20) Pageable pageable) {
        return ApiResponse.success(PageResponse.from(profileQueryUseCase.search(query.trim(), pageable), ProfileResponse::from));
    }
}
