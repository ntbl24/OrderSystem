package com.abug.profile.infrastructure.web.dto;

import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UpdateMyProfileRequest(
        @Size(max = 100) String firstName,
        @Size(max = 100) String lastName,
        LocalDate dob,
        @Size(max = 100) String city) {
}
