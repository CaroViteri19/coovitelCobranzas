package coovitelCobranza.cobranzas.casemanagement.application.dto;

/**
 * DTO para solicitar la creación de un nuevo caso de cobranza.
 *
 * @param obligationId el ID de la obligación para la cual se crea el caso
 * @param priority el nivel de prioridad del caso (LOW, MEDIUM, HIGH, CRITICAL)
 */
public record CreateCaseRequest(
        Long obligationId,
        String priority
) {
}

