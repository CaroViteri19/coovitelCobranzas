package coovitelCobranza.cobranzas.payment.domain.event;

import coovitelCobranza.cobranzas.shared.domain.event.DomainEvent;

import java.math.BigDecimal;
import java.time.Instant;

public record PaymentConfirmedEvent(
        Long paymentId,
        Long obligationId,
        BigDecimal amount,
        Instant occurredOn
) implements DomainEvent {

    @Override
    public String eventName() {
        return "PaymentConfirmed";
    }
}

