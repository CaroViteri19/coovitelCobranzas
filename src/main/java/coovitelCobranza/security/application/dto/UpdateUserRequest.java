package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Solicitud para actualizar datos basicos de usuario por body.
 *
 * @param userId   Identificador del usuario.
 * @param phone    Número de teléfono actualizado.
 * @param email    Correo actualizado.
 */
public record UpdateUserRequest(
        @NotNull(message = "userId is required")
        Long userId,
        Long phone,
        String email,
        Boolean enabled
) {
}

