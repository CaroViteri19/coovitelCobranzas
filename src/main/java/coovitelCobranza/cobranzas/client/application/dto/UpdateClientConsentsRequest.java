package coovitelCobranza.cobranzas.client.application.dto;

/**
 * Solicitud para actualizar los consentimientos de un cliente.
 * Permite modificar las preferencias de comunicación del cliente
 * para los canales WhatsApp, SMS y correo electrónico.
 */
public record UpdateClientConsentsRequest(Long clientId,
                                          boolean acceptsWhatsApp,
                                          boolean acceptsSms,
                                          boolean acceptsEmail) {
}

