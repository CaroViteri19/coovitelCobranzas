package coovitelCobranza.cobranzas.obligacion.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record RegistrarMoraRequest(
        @Min(value = 0, message = "diasMora no puede ser negativo")
        int diasMora,
        @NotNull(message = "saldoVencido es requerido")
        @DecimalMin(value = "0.00", inclusive = true, message = "saldoVencido no puede ser negativo")
        BigDecimal saldoVencido
) {
}
