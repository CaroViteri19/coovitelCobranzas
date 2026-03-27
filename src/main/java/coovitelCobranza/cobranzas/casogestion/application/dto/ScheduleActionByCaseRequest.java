package coovitelCobranza.cobranzas.casogestion.application.dto;

import java.time.LocalDateTime;

public record ScheduleActionByCaseRequest(Long caseId, LocalDateTime actionAt) {
}

