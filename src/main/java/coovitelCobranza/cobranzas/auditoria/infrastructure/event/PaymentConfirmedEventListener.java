package coovitelCobranza.cobranzas.auditoria.infrastructure.event;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import coovitelCobranza.cobranzas.pago.domain.event.PaymentConfirmedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmedEventListener {

    private final AuditService auditoriaService;

    public PaymentConfirmedEventListener(AuditService auditoriaService) {
        this.auditoriaService = auditoriaService;
    }

    @EventListener
    public void handle(PaymentConfirmedEvent event) {
        auditoriaService.registerEvent(
                "COLLECTION",
                "PAYMENT",
                event.pagoId(),
                "PAYMENT_CONFIRMED",
                "system",
                "SYSTEM",
                "EVENT",
                "Payment confirmed amount=" + event.valor() + " obligation=" + event.obligacionId(),
                event.eventName() + "-" + event.pagoId()
        );
    }
}

