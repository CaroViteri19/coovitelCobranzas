package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterUserRequest(
        @NotBlank(message = "username is required")
        @Size(min = 4, max = 80, message = "username must be between 4 and 80 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 8, max = 120, message = "password must be between 8 and 120 characters")
        String password,

        @NotBlank(message = "fullName is required")
        @Size(max = 150, message = "fullName must not exceed 150 characters")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email must not exceed 120 characters")
        String email
) {
}

