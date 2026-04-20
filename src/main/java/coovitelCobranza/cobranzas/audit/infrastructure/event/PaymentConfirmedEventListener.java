package coovitelCobranza.cobranzas.audit.infrastructure.event;

import coovitelCobranza.cobranzas.audit.domain.service.AuditService;
import coovitelCobranza.cobranzas.payment.domain.event.PaymentConfirmedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentConfirmedEventListener {

    private final AuditService auditService;

    public PaymentConfirmedEventListener(AuditService auditService) {
        this.auditService = auditService;
    }

    @EventListener
    public void handle(PaymentConfirmedEvent event) {
        auditService.registerEvent(
                "COLLECTION",
                "PAYMENT",
                event.paymentId(),
                "PAYMENT_CONFIRMED",
                "system",
                "SYSTEM",
                "EVENT",
                "Payment confirmed amount=" + event.amount() + " obligation=" + event.obligationId(),
                event.eventName() + "-" + event.paymentId()
        );
    }
}

