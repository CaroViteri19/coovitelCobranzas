package coovitelCobranza.cobranzas.casemanagement.application.dto;

/**
 * DTO para solicitar la búsqueda de un caso por su identificador.
 *
 * @param caseId el ID del caso a recuperar
 */
public record GetCaseByIdRequest(Long caseId) {
}

