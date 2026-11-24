package com.abug.profile.infrastructure.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record CreateProfileRequest(
        @NotNull UUID userId,
        @NotBlank @Size(max = 100) String username,
        @NotBlank @Email @Size(max = 320) String email) {
}
