package coovitelCobranza.cobranzas.payment.domain.gateway;

/**
 * Excepción técnica lanzada por una implementación de {@link PaymentProvider}
 * cuando la pasarela responde con error o se cae la comunicación.
 *
 * <p>El caso de uso puede envolverla en una {@code PaymentBusinessException}
 * antes de devolver error HTTP al cliente.</p>
 */
public class PaymentGatewayException extends RuntimeException {

    public PaymentGatewayException(String message) {
        super(message);
    }

    public PaymentGatewayException(String message, Throwable cause) {
        super(message, cause);
    }
}
