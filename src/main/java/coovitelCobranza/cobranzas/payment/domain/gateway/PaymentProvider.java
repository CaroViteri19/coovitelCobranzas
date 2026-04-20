package coovitelCobranza.cobranzas.payment.domain.gateway;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;

/**
 * Puerto de salida hacia una pasarela de pago.
 *
 * <p>Define el contrato que cualquier proveedor (Wompi, PayU, Mercado Pago,
 * Mock, etc.) debe cumplir para que el caso de uso de generación de link no
 * dependa de ninguna tecnología concreta.</p>
 *
 * <p>Se implementa en la capa {@code infrastructure} y se selecciona en
 * tiempo de ejecución mediante {@code @ConditionalOnProperty} sobre la
 * propiedad {@code app.payments.provider}.</p>
 */
public interface PaymentProvider {

    /**
     * Solicita a la pasarela la creación de un link de pago para una obligación.
     *
     * @param obligation obligación a cobrar (ya validada por el caso de uso).
     * @return datos del link generado (url, token, expiración, referencia).
     * @throws PaymentGatewayException si la pasarela responde con error.
     */
    PaymentLinkResponse generateLink(Obligation obligation);
}
