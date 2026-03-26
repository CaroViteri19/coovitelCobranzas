package cooviteCobranza.security.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO: RegisterUserRequest
 * 
 * Utilizado en la capa de aplicación para:
 * 1. Recibir datos de registro de nuevo usuario
 * 2. Validar entrada del cliente
 * 3. Transferir datos entre controlador y servicio de aplicación
 */
public record RegisterUserRequest(
        @NotBlank(message = "El nombre es requerido")
        @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
        String name,
        
        @NotBlank(message = "El apellido es requerido")
        @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
        String lastname,
        
        @Email(message = "El email debe ser válido")
        @NotBlank(message = "El email es requerido")
        String email,
        
        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
        String password
) {
    /**
     * Validación compacta del constructor
     */
    public RegisterUserRequest {
        if (name != null) {
            name = name.trim();
        }
        if (lastname != null) {
            lastname = lastname.trim();
        }
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}

