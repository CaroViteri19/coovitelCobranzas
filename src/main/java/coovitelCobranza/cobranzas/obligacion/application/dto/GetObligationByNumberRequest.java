package coovitelCobranza.cobranzas.obligacion.application.dto;

/**
 * DTO para solicitar una obligación por su número.
 *
 * @param obligationNumber Número único de la obligación a recuperar
 */
public record GetObligationByNumberRequest(String obligationNumber) {
}

