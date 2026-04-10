package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Registro de solicitud de autenticación de usuario.
 * Contiene las credenciales necesarias para iniciar sesión en el sistema.
 *
 * @param email Nombre de usuario (requerido).
 * @param password Contraseña del usuario (requerida).
 */
public record LoginRequest(
        @NotBlank(message = "username is required")
        String email,

        @NotBlank(message = "password is required")
        String password
) {
}

