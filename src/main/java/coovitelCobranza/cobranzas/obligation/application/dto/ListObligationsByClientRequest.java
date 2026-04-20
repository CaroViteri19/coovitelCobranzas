package coovitelCobranza.cobranzas.obligation.application.dto;

/**
 * DTO para solicitar el listado de obligaciones de un cliente.
 *
 * @param clientId Identificador único del cliente
 */
public record ListObligationsByClientRequest(Long clientId) {
}

