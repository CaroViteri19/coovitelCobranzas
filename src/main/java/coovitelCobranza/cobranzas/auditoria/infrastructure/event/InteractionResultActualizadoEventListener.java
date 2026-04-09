package coovitelCobranza.cobranzas.auditoria.infrastructure.event;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import coovitelCobranza.cobranzas.interaccion.domain.event.InteractionResultActualizadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteractionResultActualizadoEventListener {

    private final AuditService auditoriaService;

    public InteractionResultActualizadoEventListener(AuditService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @EventListener
    public void handle(InteractionResultActualizadoEvent event) {
        auditoriaService.registerEvent(
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

