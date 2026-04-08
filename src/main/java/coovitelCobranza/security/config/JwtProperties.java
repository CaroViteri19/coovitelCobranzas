package coovitelCobranza.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para JWT.
 * Mapea las propiedades de la aplicación prefijadas con "app.security.jwt".
 */
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperties {

    /**
     * Clave secreta para firmar los tokens JWT.
     */
    private String secret;

    /**
     * Emisor del token JWT.
     */
    private String issuer;

    /**
     * Tiempo de expiración del token en minutos.
     */
    private long expirationMinutes;

    /**
     * Obtiene la clave secreta.
     *
     * @return Clave secreta para firmar JWT.
     */
    public String getSecret() {
        return secret;
    }

    /**
     * Establece la clave secreta.
     *
     * @param secret Clave secreta para firmar JWT.
     */
    public void setSecret(String secret) {
        this.secret = secret;
    }

    /**
     * Obtiene el emisor del token.
     *
     * @return Emisor del token JWT.
     */
    public String getIssuer() {
        return issuer;
    }

    /**
     * Establece el emisor del token.
     *
     * @param issuer Emisor del token JWT.
     */
    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    /**
     * Obtiene el tiempo de expiración en minutos.
     *
     * @return Minutos hasta la expiración del token.
     */
    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    /**
     * Establece el tiempo de expiración en minutos.
     *
     * @param expirationMinutes Minutos hasta la expiración del token.
     */
    public void setExpirationMinutes(long expirationMinutes) {
        this.expirationMinutes = expirationMinutes;
    }
}

