package coovitelCobranza.cobranzas.payment.infrastructure.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para el adaptador Wompi (Bancolombia).
 *
 * <p>Se externalizan en {@code application.yml} bajo {@code app.payments.wompi.*}.
 * Los valores sensibles (llaves privadas, secret del webhook) deben venir por
 * variables de entorno, nunca hardcodeadas.</p>
 */
@ConfigurationProperties(prefix = "app.payments.wompi")
public class WompiProperties {

    /** URL base de la API de Wompi (ej: https://sandbox.wompi.co/v1). */
    private String baseUrl;

    /** Llave pública (aparece en las URLs de checkout). */
    private String publicKey;

    /** Llave privada para firmar peticiones server-to-server. */
    private String privateKey;

    /** Secret para validar HMAC de los eventos/webhooks entrantes. */
    private String eventsSecret;

    /** URL a la que Wompi redirige tras el pago. */
    private String redirectUrl;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }

    public String getPublicKey() { return publicKey; }
    public void setPublicKey(String publicKey) { this.publicKey = publicKey; }

    public String getPrivateKey() { return privateKey; }
    public void setPrivateKey(String privateKey) { this.privateKey = privateKey; }

    public String getEventsSecret() { return eventsSecret; }
    public void setEventsSecret(String eventsSecret) { this.eventsSecret = eventsSecret; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }
}
