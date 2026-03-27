package cooviteCobranza.cobranzas.auditoria.infrastructure.event;

import cooviteCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
import cooviteCobranza.cobranzas.interaccion.domain.event.InteraccionResultadoActualizadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class InteraccionResultadoActualizadoEventListener {

    private final AuditoriaService auditoriaService;

    public InteraccionResultadoActualizadoEventListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @EventListener
    public void handle(InteraccionResultadoActualizadoEvent event) {
        auditoriaService.registrarEvento(
                "INTERACCION",
                event.interaccionId(),
                "RESULTADO_ACTUALIZADO",
                "sistema",
                "Resultado actualizado a " + event.resultado() + " para caso " + event.casoGestionId()
        );
    }
}

