package coovitelCobranza.security.application.dto;

/**
 * Respuesta con la información básica de un rol del sistema.
 *
 * @param id          Identificador único del rol en la base de datos.
 * @param name        Nombre del rol (ej: "ADMINISTRADOR", "AGENTE").
 * @param description Descripción del rol.
 */
public record RoleResponse(
        Long id,
        String name,
        String description
) {
}
