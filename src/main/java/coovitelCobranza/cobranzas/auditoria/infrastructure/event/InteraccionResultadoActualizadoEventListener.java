package coovitelCobranza.cobranzas.auditoria.infrastructure.event;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
import coovitelCobranza.cobranzas.interaccion.domain.event.InteraccionResultadoActualizadoEvent;
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

