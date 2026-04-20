package coovitelCobranza.security.application.dto;

import java.time.LocalDateTime;

/**
 * @param id
 * @param fullname
 * @param email
 * @param role
 * @param lastSeen
 * @param status
 */
public record UserResponse(
        Long id,
        String fullname,
        String email,
        String role,
        LocalDateTime lastSeen,
        String status

) {
}
