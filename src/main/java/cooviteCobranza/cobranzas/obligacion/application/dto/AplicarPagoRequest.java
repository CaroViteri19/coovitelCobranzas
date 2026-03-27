package cooviteCobranza.cobranzas.obligacion.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AplicarPagoRequest(
        @NotNull(message = "valorPago es requerido")
        @DecimalMin(value = "0.01", message = "valorPago debe ser mayor a cero")
        BigDecimal valorPago
) {
}

