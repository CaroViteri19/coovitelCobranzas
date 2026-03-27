package cooviteCobranza.cobranzas.pago.domain.event;

import cooviteCobranza.cobranzas.shared.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record PagoConfirmadoEvent(
        Long pagoId,
        Long obligacionId,
        BigDecimal valor,
        Instant occurredOn
) implements DomainEvent {

    @Override
    public String eventName() {
        return "PagoConfirmado";
    }
}

