package coovitelCobranza.cobranzas.casemanagement.domain.event;

import coovitelCobranza.cobranzas.shared.domain.event.DomainEvent;

import java.time.Instant;

/**
 * Evento de dominio emitido cuando un Case cambia de estado.
 *
 * <p>Se publica desde el caso de uso de gestión de casos tras una transición
 * exitosa. Lo consumen, entre otros, el listener de auditoría y el orquestador
 * de pagos — por ejemplo, cuando un Case pasa a {@code PAYMENT_PROMISE} se
 * dispara la generación y envío multicanal del link de pago.</p>
 *
 * @param caseId         id del caso.
 * @param obligationId   obligación asociada (conveniencia para consumidores).
 * @param previousStatus estado anterior (nombre del enum).
 * @param newStatus      estado al que se transicionó.
 * @param reason         motivo registrado por usuario/sistema (puede ser null).
 * @param performedBy    username del actor (puede ser "system").
 * @param occurredOn     timestamp de la transición.
 */
public record CaseStatusChangedEvent(
        Long caseId,
        Long obligationId,
        String previousStatus,
        String newStatus,
        String reason,
        String performedBy,
        Instant occurredOn
) implements DomainEvent {

    @Override
    public String eventName() {
        return "CaseStatusChanged";
    }
}
