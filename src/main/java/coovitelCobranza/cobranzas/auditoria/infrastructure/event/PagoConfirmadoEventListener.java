package coovitelCobranza.cobranzas.auditoria.infrastructure.event;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
import coovitelCobranza.cobranzas.pago.domain.event.PagoConfirmadoEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PagoConfirmadoEventListener {

    private final AuditoriaService auditoriaService;

    public PagoConfirmadoEventListener(AuditoriaService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @EventListener
    public void handle(PagoConfirmadoEvent event) {
        auditoriaService.registrarEvento(
                "PAGO",
                event.pagoId(),
                "CONFIRMADO",
                "sistema",
                "Pago confirmado por valor " + event.valor() + " para obligacion " + event.obligacionId()
        );
    }
}

