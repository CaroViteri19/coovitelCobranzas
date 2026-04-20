package coovitelCobranza.security.application.dto;

import java.time.LocalDateTime;

/**
 * Resumen de usuario para listados de administracion.
 *
 * @param id       Identificador unico del usuario.
 * @param fullname Nombre completo del usuario.
 * @param email    Correo electronico del usuario.
 * @param role     Rol principal del usuario.
 * @param lastSeen Fecha/hora de ultima actualizacion del usuario.
 * @param status   Estado en texto para frontend (Active o Inactive).
 */
public record UserSummaryResponse(
        Long id,
        String fullname,
        String email,
        String role,
        LocalDateTime lastSeen,
        String status
) {
}

