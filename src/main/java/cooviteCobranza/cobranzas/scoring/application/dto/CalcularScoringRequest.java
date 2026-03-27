package cooviteCobranza.cobranzas.scoring.application.dto;

import java.math.BigDecimal;

public record CalcularScoringRequest(
        Long clienteId,
        Long obligacionId,
        int diasMora,
        BigDecimal saldoVencido,
        int intentosContacto
) {
}

