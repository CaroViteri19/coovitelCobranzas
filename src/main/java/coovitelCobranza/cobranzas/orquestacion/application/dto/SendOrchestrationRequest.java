package coovitelCobranza.cobranzas.orquestacion.application.dto;

public record SendOrchestrationRequest(
        Long casoGestionId,
        String canal,
        String destino,
        String plantilla
) {
}

