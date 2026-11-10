package com.abug.identity.application.port.in;



public record CreateUserCommand(
        String username,
        String password,
        String email
        ) {
}
