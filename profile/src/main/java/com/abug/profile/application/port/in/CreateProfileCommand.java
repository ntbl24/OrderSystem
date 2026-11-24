package com.abug.profile.application.port.in;

import java.util.UUID;

public record CreateProfileCommand(UUID userId, String username, String email) {
}
