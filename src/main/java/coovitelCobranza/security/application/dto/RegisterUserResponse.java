package coovitelCobranza.security.application.dto;

import java.util.List;

public record RegisterUserResponse(
        Long id,
        String username,
        String fullName,
        String email,
        List<String> roles,
        boolean enabled
) {
}

