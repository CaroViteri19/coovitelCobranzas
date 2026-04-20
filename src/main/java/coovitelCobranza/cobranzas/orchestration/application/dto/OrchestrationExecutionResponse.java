package coovitelCobranza.cobranzas.orchestration.application.dto;

import coovitelCobranza.cobranzas.orchestration.domain.model.OrchestrationExecution;

import java.time.LocalDateTime;

public record OrchestrationExecutionResponse(
        Long id,
        Long casoGestionId,
        String canal,
        String destino,
        String plantilla,
        String estado,
        LocalDateTime createdAt
) {

    public static OrchestrationExecutionResponse fromDomain(OrchestrationExecution ejecucion) {
        return new OrchestrationExecutionResponse(
                ejecucion.getId(),
                ejecucion.getCaseId(),
                ejecucion.getChannel(),
                ejecucion.getDestino(),
                ejecucion.getTemplate(),
                ejecucion.getStatus().name(),
                ejecucion.getCreatedAt()
        );
    }
}

