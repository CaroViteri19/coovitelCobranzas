package coovitelCobranza.cobranzas.obligation.application.dto;

/**
 * DTO para solicitar una obligación por su identificador único.
 *
 * @param obligationId Identificador único de la obligación a recuperar
 */
public record GetObligationByIdRequest(Long obligationId) {
}

