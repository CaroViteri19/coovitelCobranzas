package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Solicitud para activar o desactivar un usuario por body.
 *
 * @param userId  Identificador del usuario a actualizar.
 * @param enabled Nuevo estado habilitado del usuario.
 */
public record UpdateUserStatusRequest(
        @NotNull(message = "userId is required")
        Long userId,

        @NotNull(message = "enabled is required")
        Boolean enabled
) {
}

