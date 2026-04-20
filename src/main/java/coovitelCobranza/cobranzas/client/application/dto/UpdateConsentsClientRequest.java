package coovitelCobranza.cobranzas.client.application.dto;

/**
 * Solicitud alternativa para actualizar los consentimientos de un cliente.
 * Especifica las preferencias de comunicación del cliente sin incluir el ID.
 */
public record UpdateConsentsClientRequest(
        boolean aceptaWhatsApp,
        boolean aceptaSms,
        boolean aceptaEmail
) {
}

