package com.abug.profile.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateAvatarRequest(
        @NotBlank @Size(max = 2048) String avatar) {
}
