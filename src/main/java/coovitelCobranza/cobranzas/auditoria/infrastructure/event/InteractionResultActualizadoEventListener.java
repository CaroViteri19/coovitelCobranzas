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
        auditoriaService.registrarEvent(
                "INTERACCION",
                event.interactionId(),
                "RESULTADO_ACTUALIZADO",
                "sistema",
                "Result actualizado a " + event.resultado() + " para caso " + event.casoGestionId()
        );
    }
}

