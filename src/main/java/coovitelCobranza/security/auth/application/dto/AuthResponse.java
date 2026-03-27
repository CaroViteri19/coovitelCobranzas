package coovitelCobranza.security.auth.application.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO: AuthResponse
 * 
 * Utilizado en la capa de aplicación para:
 * 1. Retornar token JWT después de autenticación exitosa
 * 2. Seguir el estándar OAuth2 para respuestas de token
 * 3. Transferir datos entre servicio de aplicación y cliente
 */
public record AuthResponse(
        @JsonProperty("access_token")
        String accessToken,
        
        @JsonProperty("token_type")
        String tokenType,
        
        @JsonProperty("expires_in")
        long expiresIn,
        
        @JsonProperty("refresh_token")
        String refreshToken,
        
        @JsonProperty("user")
        UserResponse user
) {
    /**
     * Constructor simplificado para casos sin refresh token
     */
    public AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {
        this(accessToken, tokenType, expiresIn, null, user);
    }
}


