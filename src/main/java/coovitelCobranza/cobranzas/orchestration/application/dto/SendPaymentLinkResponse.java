package coovitelCobranza.cobranzas.orchestration.application.dto;

import java.util.List;

/**
 * Resumen devuelto por el use case
 * {@code PaymentOrchestrationApplicationService.generateAndSendLinkForCase}.
 *
 * @param caseId             id del caso gestionado.
 * @param obligationId       id de la obligación asociada.
 * @param paymentId          id del {@code Payment} PENDING recién creado.
 * @param paymentUrl         url pública que se compartió con el cliente.
 * @param channelsAttempted  cantidad de canales en los que se intentó enviar.
 * @param channelsDelivered  cantidad de canales reportados como DELIVERED.
 * @param dispatches         detalle por canal.
 */
public record SendPaymentLinkResponse(
        Long caseId,
        Long obligationId,
        Long paymentId,
        String paymentUrl,
        int channelsAttempted,
        int channelsDelivered,
        List<ChannelDispatchResult> dispatches
) {
}
