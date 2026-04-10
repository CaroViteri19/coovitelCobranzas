package coovitelCobranza.security.application.dto;

import java.util.List;

/**
 * Registro de respuesta tras el registro exitoso de un nuevo usuario.
 * Contiene la información del usuario creado.
 *
 * @param id Identificador único del usuario.
 * @param username Nombre de usuario creado.
 * @param fullName Nombre completo del usuario.
 * @param email Correo electrónico del usuario.
 * @param roles Lista de roles asignados al usuario.
 * @param enabled Indica si la cuenta está habilitada.
 */
public record RegisterUserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        String roles,
        boolean enabled
) {     
}

