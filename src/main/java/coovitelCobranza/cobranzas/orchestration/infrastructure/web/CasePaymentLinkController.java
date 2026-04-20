package coovitelCobranza.cobranzas.orchestration.infrastructure.web;

import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkRequest;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendPaymentLinkResponse;
import coovitelCobranza.cobranzas.orchestration.application.service.PaymentOrchestrationApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador REST que expone el envío manual del link de pago para un caso.
 *
 * <p>Vive en el módulo de orquestación porque su responsabilidad es coordinar
 * Payment + Interaction. La URL {@code /api/v1/cases/{caseId}/send-payment-link}
 * se mantiene en el espacio de nombres de casos para facilitar el consumo
 * desde el frontend.</p>
 */
@RestController
@RequestMapping("/api/v1/cases")
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class CasePaymentLinkController {

    private final PaymentOrchestrationApplicationService paymentOrchestrationService;

    /**
     * Genera un link de pago y lo envía a los canales consentidos del cliente
     * (o a los canales explícitos del request).
     *
     * @param caseId id del caso.
     * @param request cuerpo opcional. Si es {@code null} se usan defaults.
     * @return 200 con el resumen del envío.
     */
    @PreAuthorize("hasAnyRole('ADMINISTRADOR','SUPERVISOR','AGENTE')")
    @PostMapping("/{caseId}/send-payment-link")
    public ResponseEntity<SendPaymentLinkResponse> sendPaymentLink(
            @PathVariable Long caseId,
            @RequestBody(required = false) SendPaymentLinkRequest request) {

        SendPaymentLinkResponse response = paymentOrchestrationService
                .generateAndSendLinkForCase(caseId, request);
        return ResponseEntity.ok(response);
    }
}
