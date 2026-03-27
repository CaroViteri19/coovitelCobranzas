package cooviteCobranza.cobranzas.casogestion.application.dto;

import java.time.LocalDateTime;

public record ProgramarAccionRequest(
        LocalDateTime fechaHora
) {
}

