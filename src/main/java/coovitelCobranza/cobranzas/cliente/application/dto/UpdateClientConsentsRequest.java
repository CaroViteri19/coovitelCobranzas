package coovitelCobranza.cobranzas.cliente.application.dto;

public record UpdateClientConsentsRequest(Long clientId,
                                          boolean acceptsWhatsApp,
                                          boolean acceptsSms,
                                          boolean acceptsEmail) {
}

