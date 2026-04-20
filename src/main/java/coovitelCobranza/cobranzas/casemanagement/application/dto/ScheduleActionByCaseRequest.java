package coovitelCobranza.cobranzas.casemanagement.application.dto;

import java.time.LocalDateTime;

/**
 * DTO para programar una acción futura en un caso específico.
 *
 * @param caseId el ID del caso en el cual se programará la acción
 * @param actionAt la fecha y hora en que debe ejecutarse la acción
 */
public record ScheduleActionByCaseRequest(Long caseId, LocalDateTime actionAt) {
}

