package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotNull;

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

