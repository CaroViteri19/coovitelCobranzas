package cooviteCobranza.security.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterUserRequest(
        @NotBlank String name,
        @NotBlank String lastname,
        @Email @NotBlank String email,
        @NotBlank String password
) {
}

