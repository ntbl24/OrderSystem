package com.abug.chat_service.infrastructure.external.httpclient.dto;

import java.time.LocalDate;

public record ProfileUserResponse(
        String id,
        String userId,
        String avatar,
        String username,
        String email,
        String firstName,
        String lastName,
        LocalDate dob,
        String city
) {
}
