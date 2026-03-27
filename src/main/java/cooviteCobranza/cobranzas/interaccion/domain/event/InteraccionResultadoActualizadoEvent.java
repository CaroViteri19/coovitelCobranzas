package cooviteCobranza.cobranzas.interaccion.domain.event;

import cooviteCobranza.cobranzas.shared.domain.event.DomainEvent;

import java.time.Instant;

public record InteraccionResultadoActualizadoEvent(
		Long interaccionId,
		Long casoGestionId,
		String resultado,
		Instant occurredOn
) implements DomainEvent {

	@Override
	public String eventName() {
		return "InteraccionResultadoActualizado";
	}
}

