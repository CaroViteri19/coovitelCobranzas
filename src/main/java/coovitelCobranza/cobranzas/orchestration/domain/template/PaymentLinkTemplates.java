package coovitelCobranza.cobranzas.orchestration.domain.template;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;

/**
 * Catálogo de plantillas para el envío del link de pago por canal.
 *
 * <p>Las plantillas usan los placeholders:</p>
 * <ul>
 *   <li>{@code {{fullName}}}     — nombre completo del cliente.</li>
 *   <li>{@code {{obligationNumber}}} — número de obligación visible al cliente.</li>
 *   <li>{@code {{amount}}}       — monto a pagar (ya formateado en pesos).</li>
 *   <li>{@code {{url}}}          — link de pago.</li>
 *   <li>{@code {{expirationDate}}} — cuándo caduca el link.</li>
 * </ul>
 *
 * <p>Las cadenas son concretas y simples a propósito: el negocio puede
 * afinarlas sin tocar código. En el futuro se pueden externalizar a base de
 * datos/archivo para permitir edición por operaciones.</p>
 */
public final class PaymentLinkTemplates {

    /** Clave usada para identificar la plantilla en {@code Interaction.template}. */
    public static final String TEMPLATE_KEY = "PAYMENT_LINK";

    public static final String EMAIL_SUBJECT =
            "Coovitel: link de pago de tu obligación {{obligationNumber}}";

    public static final String EMAIL_BODY = """
            Hola {{fullName}},

            Te compartimos el link de pago de tu obligación {{obligationNumber}}
            por valor de ${{amount}}. El enlace expira el {{expirationDate}}.

            Paga aquí: {{url}}

            Si ya realizaste el pago, ignora este mensaje.

            — Coovitel Cobranzas
            """;

    public static final String SMS_BODY =
            "Coovitel: {{fullName}}, paga tu obligacion {{obligationNumber}} por ${{amount}} aqui: {{url}} (vence {{expirationDate}})";

    public static final String WHATSAPP_BODY = """
            Hola {{fullName}} 👋
            Este es el link para el pago de tu obligación *{{obligationNumber}}* por *${{amount}}*:
            {{url}}
            Vence: {{expirationDate}}
            """;

    private PaymentLinkTemplates() {
        // util
    }

    /**
     * Devuelve el cuerpo correspondiente al canal indicado.
     *
     * @throws IllegalArgumentException si el canal no tiene plantilla (VOICE).
     */
    public static String bodyFor(Interaction.Channel channel) {
        return switch (channel) {
            case EMAIL    -> EMAIL_BODY;
            case SMS      -> SMS_BODY;
            case WHATSAPP -> WHATSAPP_BODY;
            case VOICE    -> throw new IllegalArgumentException(
                    "VOICE channel has no payment-link template");
        };
    }

    /**
     * Devuelve el asunto correspondiente al canal. Sólo EMAIL lo usa; para
     * los demás se devuelve {@code null}.
     */
    public static String subjectFor(Interaction.Channel channel) {
        return channel == Interaction.Channel.EMAIL ? EMAIL_SUBJECT : null;
    }
}
