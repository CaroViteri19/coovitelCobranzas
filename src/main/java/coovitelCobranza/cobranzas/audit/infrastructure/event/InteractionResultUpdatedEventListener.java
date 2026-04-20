package coovitelCobranza.cobranzas.audit.infrastructure.event;

import coovitelCobranza.cobranzas.audit.domain.service.AuditService;
import coovitelCobranza.cobranzas.interaction.domain.event.InteractionResultActualizadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionResultUpdatedEventListener {

    private final AuditService auditService;

    public InteractionResultUpdatedEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void handle(InteractionResultActualizadoEvent event) {
        auditService.registerEvent(
                "ORCHESTRATION",
                "INTERACTION",
                event.interactionId(),
                "INTERACTION_RESULT_UPDATED",
                "system",
                "SYSTEM",
                "EVENT",
                "Result updated to " + event.resultado() + " for case " + event.casoGestionId(),
                event.eventName() + "-" + event.interactionId()
        );
    }
}


