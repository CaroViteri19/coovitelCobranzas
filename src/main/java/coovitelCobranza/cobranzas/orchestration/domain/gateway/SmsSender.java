package coovitelCobranza.cobranzas.orchestration.domain.gateway;

/**
 * Puerto de salida para el canal SMS. Las implementaciones concretas
 * (Twilio, Infobip, Avantel, stub) viven en la capa infra.
 */
public interface SmsSender {

    DeliveryResult send(OutboundMessage message);
}
