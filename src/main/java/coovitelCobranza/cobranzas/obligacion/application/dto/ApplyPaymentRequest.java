package coovitelCobranza.cobranzas.obligacion.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ApplyPaymentRequest(
        @NotNull(message = "valorPayment es requerido")
        @DecimalMin(value = "0.01", message = "valorPayment debe ser mayor a cero")
        BigDecimal valorPayment
) {
}

