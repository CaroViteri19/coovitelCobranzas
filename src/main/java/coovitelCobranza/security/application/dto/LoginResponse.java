package coovitelCobranza.security.application.dto;

import java.time.Instant;
import java.util.List;

/**
 * Registro de respuesta de autenticación exitosa.
 * Contiene el token JWT y la información del usuario autenticado.
 *
 * @param token Token JWT generado para el usuario.
 * @param tokenType Tipo de token (ej: "Bearer").
 * @param username Nombre de usuario autenticado.
 * @param roles Lista de roles asignados al usuario.
 * @param expiresAt Fecha y hora de expiración del token.
 */
public record LoginResponse(
        String token,
        String tokenType,
        String username,
        List<String> roles,
        Instant expiresAt
) {
}

