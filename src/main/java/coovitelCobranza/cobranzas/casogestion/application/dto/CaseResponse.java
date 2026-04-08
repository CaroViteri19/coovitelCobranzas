package coovitelCobranza.cobranzas.casogestion.application.dto;

import coovitelCobranza.cobranzas.casogestion.domain.model.Case;

import java.time.LocalDateTime;

/**
 * DTO de respuesta que representa la información completa de un caso de cobranza.
 *
 * @param id el identificador único del caso
 * @param obligationId el ID de la obligación asociada al caso
 * @param priority el nivel de prioridad del caso
 * @param status el estado actual del caso
 * @param assignedAdvisor el nombre del asesor asignado al caso
 * @param nextActionAt la fecha y hora de la próxima acción programada
 * @param updatedAt la fecha y hora de la última actualización del caso
 */
public record CaseResponse(
        Long id,
        Long obligationId,
        String priority,
        String status,
        String assignedAdvisor,
        LocalDateTime nextActionAt,
        LocalDateTime updatedAt
) {

    /**
     * Convierte un modelo de dominio Case a un DTO CaseResponse.
     *
     * @param casoGestion el caso del dominio
     * @return un DTO con la información del caso
     */
    public static CaseResponse fromDomain(Case casoGestion) {
        return new CaseResponse(
                casoGestion.getId(),
                casoGestion.getObligationId(),
                casoGestion.getPriority().name(),
                casoGestion.getStatus().name(),
                casoGestion.getAssignedAdvisor(),
                casoGestion.getNextActionAt(),
                casoGestion.getUpdatedAt()
        );
    }
}

