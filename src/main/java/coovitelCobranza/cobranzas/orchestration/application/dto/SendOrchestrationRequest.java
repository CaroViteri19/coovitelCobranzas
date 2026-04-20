package coovitelCobranza.cobranzas.orchestration.application.dto;

public record SendOrchestrationRequest(
        Long casoGestionId,
        String canal,
        String destino,
        String plantilla
) {
}

