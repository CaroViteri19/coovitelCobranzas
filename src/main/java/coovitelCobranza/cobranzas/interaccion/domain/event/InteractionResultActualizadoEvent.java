package coovitelCobranza.cobranzas.interaccion.domain.event;

import coovitelCobranza.cobranzas.shared.domain.event.DomainEvent;

import java.time.Instant;

public record InteractionResultActualizadoEvent(
		Long interactionId,
		Long casoGestionId,
		String resultado,
		Instant occurredOn
) implements DomainEvent {

	@Override
	public String eventName() {
		return "InteractionResultActualizado";
	}
}

