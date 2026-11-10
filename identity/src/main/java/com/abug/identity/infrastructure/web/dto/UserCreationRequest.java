package com.abug.identity.infrastructure.web.dto;

import com.abug.identity.application.port.in.CreateUserCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserCreationRequest(
        @Size(min = 4, message = "USERNAME_INVALID")
        String username,

        @Size(min = 6, message = "INVALID_PASSWORD")
                String password,
        @Email(message = "INVALID_EMAIL")
        @NotBlank(message = "EMAIL_IS_REQUIRED")
        String email
) {
        public CreateUserCommand toCommand(){
                return new CreateUserCommand(username, password, email);
        }
}
