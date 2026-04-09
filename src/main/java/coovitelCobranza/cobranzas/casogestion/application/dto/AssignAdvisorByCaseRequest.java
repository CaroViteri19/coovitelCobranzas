package coovitelCobranza.cobranzas.casogestion.application.dto;

/**
 * DTO para asignar un asesor a un caso específico.
 *
 * @param caseId el ID del caso al que se asignará el asesor
 * @param advisor el nombre o identificador del asesor
 */
public record AssignAdvisorByCaseRequest(
        Long caseId,
        String advisor,
        String performedBy,
        String performedByRole,
        String assignmentSource,
        String correlationId
) {
}

