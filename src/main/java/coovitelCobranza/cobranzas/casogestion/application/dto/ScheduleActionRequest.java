package coovitelCobranza.cobranzas.casogestion.application.dto;

import java.time.LocalDateTime;

public record ScheduleActionRequest(
        LocalDateTime dateTime
) {
}

