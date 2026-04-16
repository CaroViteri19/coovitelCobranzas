package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para actualizar datos basicos de usuario por body.
 *
 * @param userId   Identificador del usuario.
 * @param fullName Nombre completo actualizado.
 * @param email    Correo actualizado.
 * @param role     Nombre del rol (opcional).
 */
public record UpdateUserRequest(
        @NotNull(message = "userId is required")
        Long userId,

        @NotBlank(message = "fullName is required")
        @Size(max = 150, message = "fullName must not exceed 150 characters")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email must not exceed 120 characters")
        String email,

        String role
) {
}

