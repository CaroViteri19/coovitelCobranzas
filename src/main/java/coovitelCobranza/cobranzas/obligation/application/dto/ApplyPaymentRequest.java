package coovitelCobranza.cobranzas.obligation.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * DTO para solicitar la aplicación de un pago.
 * Contiene el monto del pago que debe ser mayor a cero.
 *
 * @param valorPayment Monto del pago, debe ser mayor a 0.01
 */
public record ApplyPaymentRequest(
        @NotNull(message = "valorPayment es requerido")
        @DecimalMin(value = "0.01", message = "valorPayment debe ser mayor a cero")
        BigDecimal valorPayment
) {
}

