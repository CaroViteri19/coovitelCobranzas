package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.*;

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

        @NotBlank(message = "documentType is required")
        @Size(min = 2, max = 4, message = "documentType must be between 2 and 4 characters")
        String documentType,

        @NotNull(message = "document is required")
        @DecimalMin(value = "10000000", message = "document must be between 8 and 10 digits")
        @DecimalMax(value = "9999999999", message = "document must be between 8 and 10 digits")
        Long document,

        @NotBlank(message = "password is required")
        @Size(min = 12, max = 120, message = "password must be 12 and least one special character")
        String password,

        @NotBlank(message = "fullName is required")
        @Size(max = 50, message = "fullName must not exceed 50 characters")
        @Pattern(
                regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$",
                message = "fullName must only contain letters and spaces"
        )
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 120, message = "email must not exceed 120 characters")
        String email,

        @NotNull(message = "role is required")
        Long role

) {
}

