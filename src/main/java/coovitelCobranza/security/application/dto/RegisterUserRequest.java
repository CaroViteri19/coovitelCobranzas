package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Registro de solicitud para registrar un nuevo usuario en el sistema.
 * Contiene todos los datos necesarios para crear una cuenta de usuario.
 *
 * @param username Nombre de usuario (entre 4 y 80 caracteres).
 * @param password Contraseña del usuario (entre 8 y 120 caracteres).
 * @param fullName Nombre completo del usuario (máximo 150 caracteres).
 * @param email    Correo electrónico del usuario (válido, máximo 120 caracteres).
 * @param role     Identificador del rol asignado al usuario (requerido).
 */
public record RegisterUserRequest(
        @NotBlank(message = "username is required")
        @Size(min = 4, max = 80, message = "username must be between 4 and 80 characters")
        String username,

        @NotBlank(message = "password is required")
        @Size(min = 12, max = 120, message = "password must be 12 and least one special character")
        String password,

        @NotBlank(message = "fullName is required")
        @Size(max = 150, message = "fullName must not exceed 150 characters")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email must not exceed 120 characters")
        String email,

        @NotNull(message = "role is required")
        Long role

) {
}

