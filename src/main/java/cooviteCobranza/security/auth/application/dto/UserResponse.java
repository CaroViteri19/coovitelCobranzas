package cooviteCobranza.security.auth.application.dto;

import cooviteCobranza.security.auth.domain.model.User;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO: UserResponse
 * 
 * Utilizado en la capa de aplicación para:
 * 1. Transferir información del usuario hacia el cliente
 * 2. NO expone datos sensibles como la contraseña
 * 3. Contiene solo información pública del usuario
 */
public record UserResponse(
        @JsonProperty("id")
        Long id,
        
        @JsonProperty("name")
        String name,
        
        @JsonProperty("lastname")
        String lastname,
        
        @JsonProperty("email")
        String email,
        
        @JsonProperty("active")
        Boolean active,
        
        @JsonProperty("roles")
        Set<String> roles,
        
        @JsonProperty("created_at")
        LocalDateTime createdAt,
        
        @JsonProperty("last_login")
        LocalDateTime lastLogin
) {
    /**
     * Factory method para crear UserResponse desde User (dominio)
     */
    public static UserResponse fromDomain(User user) {
        Set<String> roleNames = user.getRoles().stream()
                .map(role -> role.getBasePermission())
                .collect(Collectors.toSet());
        
        return new UserResponse(
                user.getIdUser(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getActive(),
                roleNames,
                user.getCreatedAt(),
                user.getLastLogin()
        );
    }
}

