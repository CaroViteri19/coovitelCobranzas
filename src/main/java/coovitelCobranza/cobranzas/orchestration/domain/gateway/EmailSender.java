package coovitelCobranza.cobranzas.orchestration.domain.gateway;

/**
 * Puerto de salida para el canal Email / Correo electrónico. Las
 * implementaciones concretas (SendGrid, SES, SMTP, stub) viven en infra.
 */
public interface EmailSender {

    DeliveryResult send(OutboundMessage message);
}
