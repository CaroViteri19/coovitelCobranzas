package coovitelCobranza.security.auth.application.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO: LoginRequest
 * 
 * Utilizado en la capa de aplicación para:
 * 1. Recibir credenciales de login
 * 2. Validar entrada del cliente
 * 3. Transferir datos entre controlador y servicio de aplicación
 */
public record LoginRequest(
        @Email(message = "El email debe ser válido")
        @NotBlank(message = "El email es requerido")
        String email,
        
        @NotBlank(message = "La contraseña es requerida")
        @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
        String password
) {
    /**
     * Validación adicional
     */
    public LoginRequest {
        if (email != null) {
            email = email.trim().toLowerCase();
        }
    }
}

