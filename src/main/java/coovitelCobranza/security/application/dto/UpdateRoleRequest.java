package coovitelCobranza.security.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record UpdateRoleRequest(
        @NotNull(message = "idUser is required") Long idUser,
        @NotNull(message = "role is required")
        @Size(min = 1, message = "role must contain at least one role id") List<Long> role
) {
}
