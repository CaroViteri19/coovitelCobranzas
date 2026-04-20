package coovitelCobranza.cobranzas.payment.infrastructure.web.webhook;

import coovitelCobranza.cobranzas.payment.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.payment.application.dto.WebhookNotificationRequest;
import coovitelCobranza.cobranzas.payment.application.service.PaymentApplicationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador público que recibe las notificaciones (webhooks) de la pasarela
 * de pagos.
 *
 * <p>Características del endpoint:</p>
 * <ul>
 *   <li>Es <b>público</b> (no requiere JWT). Se autentica por firma HMAC del
 *       payload — validación que queda como TODO dentro del service.</li>
 *   <li>Es <b>idempotente</b>: la pasarela suele reintentar; si el Payment ya
 *       fue procesado el service devuelve el estado actual sin volver a aplicar
 *       el pago sobre la obligación.</li>
 *   <li>Devuelve 200 ante reintentos duplicados para que la pasarela deje de
 *       reenviar.</li>
 * </ul>
 *
 * <p>Nota: añadir la ruta {@code /api/v1/webhooks/payments/**} a la lista de
 * {@code permitAll()} de Spring Security.</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks/payments")
@RequiredArgsConstructor
public class PaymentWebhookController {

    private final PaymentApplicationService paymentApplicationService;

    @PostMapping
    public ResponseEntity<PaymentResponse> receive(@RequestBody WebhookNotificationRequest request) {
        log.info("Webhook pago recibido ref={} status={}",
                request.gatewayReference(), request.status());
        PaymentResponse response = paymentApplicationService.processWebhook(request);
        return ResponseEntity.ok(response);
    }
}
