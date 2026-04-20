package coovitelCobranza.cobranzas.casemanagement.application.dto;

/**
 * DTO para solicitar el cierre de un caso de cobranza.
 *
 * @param caseId el ID del caso a cerrar
 */
public record CloseCaseRequest(Long caseId) {
}

