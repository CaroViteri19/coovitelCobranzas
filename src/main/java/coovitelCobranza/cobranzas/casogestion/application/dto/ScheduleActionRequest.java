package coovitelCobranza.cobranzas.casogestion.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para solicitar la programación de una próxima acción en un caso.
 *
 * @param dateTime la fecha y hora de la próxima acción a programar
 */
public record ScheduleActionRequest(
        LocalDateTime dateTime
) {
}

