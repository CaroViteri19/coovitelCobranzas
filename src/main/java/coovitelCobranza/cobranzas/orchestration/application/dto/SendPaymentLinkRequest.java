package coovitelCobranza.cobranzas.orchestration.application.dto;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.util.Set;

/**
 * DTO de entrada para el envío del link de pago a un caso.
 *
 * <p>Todos los campos son opcionales:</p>
 * <ul>
 *   <li>{@code amount}   — si se omite se usa el {@code totalBalance} de la obligación.</li>
 *   <li>{@code method}   — si se omite se usa {@link Payment.PaymentMethod#PSE}.</li>
 *   <li>{@code channels} — si se omite se intentan todos los canales en los que
 *       el cliente haya dado consentimiento (SMS, EMAIL, WHATSAPP). VOICE se
 *       ignora siempre porque no tiene plantilla de link de pago.</li>
 * </ul>
 *
 * @param amount   monto a cobrar; si es {@code null} se toma el saldo total.
 * @param method   método propuesto al generar el link.
 * @param channels canales específicos a usar; si es {@code null} o vacío se
 *                 usan los consentidos por el cliente.
 */
public record SendPaymentLinkRequest(
        BigDecimal amount,
        Payment.PaymentMethod method,
        Set<Interaction.Channel> channels
) {

    /** Devuelve el método o el valor por defecto {@link Payment.PaymentMethod#PSE}. */
    public Payment.PaymentMethod methodOrDefault() {
        return method != null ? method : Payment.PaymentMethod.PSE;
    }

    /** {@code true} si el request incluye un subconjunto explícito de canales. */
    public boolean hasExplicitChannels() {
        return channels != null && !channels.isEmpty();
    }

    /** Constructor vacío equivalente a "usar todos los defaults". */
    public static SendPaymentLinkRequest defaults() {
        return new SendPaymentLinkRequest(null, null, null);
    }
}
