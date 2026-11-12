package com.abug.identity.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AuthenticationRequest(
        @NotNull
        @NotBlank
        String username,
        @NotBlank
        @NotNull
        String password
) {
}
