package cooviteCobranza.cobranzas.shared.domain.event;

import java.time.Instant;

public interface DomainEvent {

    String eventName();

    Instant occurredOn();
}

