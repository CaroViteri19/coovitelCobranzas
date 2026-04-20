package coovitelCobranza.cobranzas.payment.application.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Payload recibido desde la pasarela (webhook).
 *
 * <p>Usamos {@link JsonAlias} para aceptar tanto nomenclatura inglesa (Wompi/
 * estándar) como la simulada/legacy en español — así la misma clase sirve
 * contra Mock, Wompi u otras pasarelas con mapping equivalente.</p>
 *
 * @param gatewayReference referencia única de la pasarela (obligatoria).
 * @param status           estado reportado por la pasarela (APPROVED, REJECTED, ...).
 * @param amount           monto reportado (informativo; el truth está en el Payment).
 * @param signature        firma HMAC opcional para validar autenticidad.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record WebhookNotificationRequest(
        @JsonAlias({"referenciaPasarela", "reference", "gateway_reference", "externalReference", "id"})
        String gatewayReference,

        @JsonAlias({"estado", "state"})
        String status,

        @JsonAlias({"valor", "total", "amount_in_cents"})
        BigDecimal amount,

        @JsonAlias({"firma", "hmac"})
        String signature
) {
    public String statusOrEmpty() {
        return status == null ? "" : status;
    }
}
