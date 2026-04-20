package coovitelCobranza.cobranzas.orchestration.domain.gateway;

/**
 * Mensaje saliente listo para ser entregado por un {@code Sender}.
 * Contiene el texto ya renderizado (sin placeholders) y la metadata mínima
 * que cualquier canal necesita: destinatario y, cuando aplica, asunto.
 *
 * @param destination destinatario (teléfono E.164 para SMS/WhatsApp; email para EMAIL).
 * @param subject     asunto del mensaje; sólo relevante para EMAIL, puede ser null.
 * @param body        cuerpo renderizado.
 */
public record OutboundMessage(
        String destination,
        String subject,
        String body
) {
    public static OutboundMessage text(String destination, String body) {
        return new OutboundMessage(destination, null, body);
    }

    public static OutboundMessage email(String destination, String subject, String body) {
        return new OutboundMessage(destination, subject, body);
    }
}
