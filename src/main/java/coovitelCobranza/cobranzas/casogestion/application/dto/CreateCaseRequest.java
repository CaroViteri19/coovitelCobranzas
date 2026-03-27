package coovitelCobranza.cobranzas.casogestion.application.dto;

public record CreateCaseRequest(
        Long obligationId,
        String priority
) {
}

