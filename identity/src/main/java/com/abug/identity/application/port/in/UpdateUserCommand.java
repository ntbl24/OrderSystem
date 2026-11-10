package com.abug.identity.application.port.in;

import java.util.List;

public record UpdateUserCommand(
        String password,
        List<String> roles
) {
}
