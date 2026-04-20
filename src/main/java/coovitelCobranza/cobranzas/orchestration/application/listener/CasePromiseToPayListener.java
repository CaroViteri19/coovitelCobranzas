package coovitelCobranza.cobranzas.orchestration.application.listener;

import coovitelCobranza.cobranzas.casemanagement.domain.event.CaseStatusChangedEvent;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkResponse;
import coovitelCobranza.cobranzas.orchestration.application.service.PaymentOrchestrationApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener que reacciona a {@link CaseStatusChangedEvent} y, cuando el nuevo
 * estado es {@code PAYMENT_PROMISE} (promesa de pago del cliente), dispara
 * automáticamente la generación y envío multicanal del link de pago.
 *
 * <p>Se usa {@link TransactionalEventListener} con
 * {@link TransactionPhase#AFTER_COMMIT} para asegurar que la transición del
 * Case ya quedó persistida antes de enviar nada al cliente — evitamos enviar
 * un link si la transacción original terminó en rollback.</p>
 *
 * <p>Las excepciones del envío no se re-lanzan: un fallo de proveedor no
 * debería impactar al flujo original de cambio de estado del caso. Cada
 * intento por canal se registra en su propia Interaction con su resultado.</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CasePromiseToPayListener {

    private static final String PAYMENT_PROMISE = "PAYMENT_PROMISE";

    private final PaymentOrchestrationApplicationService paymentOrchestrationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCaseStatusChanged(CaseStatusChangedEvent event) {
        if (!PAYMENT_PROMISE.equals(event.newStatus())) {
            return;
        }

        log.info("CaseStatusChanged → PAYMENT_PROMISE: caseId={} (prev={}) → envío automático de link de pago",
                event.caseId(), event.previousStatus());

        try {
            SendPaymentLinkResponse response = paymentOrchestrationService
                    .generateAndSendLinkForCase(event.caseId(), SendPaymentLinkRequest.defaults());

            log.info("Envío automático completado: caseId={} paymentId={} entregados={} intentados={}",
                    event.caseId(), response.paymentId(),
                    response.channelsDelivered(), response.channelsAttempted());

        } catch (Exception ex) {
            // Los senders no lanzan excepción; si caemos aquí es que la obligación
            // ya está cancelada, el cliente no existe, la pasarela falló al crear
            // el link, etc. Se registra y NO se propaga para no romper el flujo
            // disparador — el agente humano podrá reintentar desde el endpoint
            // manual /send-payment-link.
            log.error("Fallo en envío automático de link para caseId={}: {}",
                    event.caseId(), ex.getMessage(), ex);
        }
    }
}
