package coovitelCobranza.cobranzas.orchestration.domain.gateway;

/**
 * Puerto de salida para el canal WhatsApp. Las implementaciones concretas
 * (Twilio, Meta Cloud API, Infobip, stub) viven en infra.
 */
public interface WhatsAppSender {

    DeliveryResult send(OutboundMessage message);
}
