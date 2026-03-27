package cooviteCobranza.cobranzas.pago.application.dto;

import java.math.BigDecimal;

public record CrearPagoRequest(
        Long obligacionId,
        BigDecimal valor,
        String referenciaExterna,
        String metodo
) {
}

