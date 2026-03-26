package cooviteCobranza.security.auth.application.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}

