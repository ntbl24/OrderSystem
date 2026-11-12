package com.abug.identity.infrastructure.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LogoutRequest(
    @NotNull
    @NotBlank
    String token) {}
