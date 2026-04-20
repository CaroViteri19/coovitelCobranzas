package coovitelCobranza.cobranzas.payment.application.service;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligation.domain.repository.ObligationRepository;
import coovitelCobranza.cobranzas.payment.application.dto.ConfirmPaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.CreatePaymentRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkRequest;
import coovitelCobranza.cobranzas.payment.application.dto.GenerateLinkResponse;
import coovitelCobranza.cobranzas.payment.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.payment.application.dto.WebhookNotificationRequest;
import coovitelCobranza.cobranzas.payment.application.exception.ObligationForPaymentNotFoundException;
import coovitelCobranza.cobranzas.payment.application.exception.PaymentBusinessException;
import coovitelCobranza.cobranzas.payment.application.exception.PaymentNotFoundException;
import coovitelCobranza.cobranzas.payment.domain.event.PaymentConfirmedEvent;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentLinkResponse;
import coovitelCobranza.cobranzas.payment.domain.gateway.PaymentProvider;
import coovitelCobranza.cobranzas.payment.domain.model.Payment;
import coovitelCobranza.cobranzas.payment.domain.repository.PaymentRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Locale;

/**
 * Caso de uso del módulo de pagos.
 *
 * <p>Encapsula los flujos:</p>
 * <ol>
 *   <li><b>generateLink</b>: pide a la pasarela un link, persiste un Payment PENDING.</li>
 *   <li><b>processWebhook</b>: al recibir la notificación asíncrona reconcilia
 *       el Payment y aplica el pago sobre la Obligación en una única transacción.</li>
 *   <li><b>confirmPayment</b>: confirmación manual (endpoint interno protegido),
 *       también atómica.</li>
 * </ol>
 *
 * <p>Depende SÓLO de puertos de dominio ({@link PaymentProvider},
 * {@link PaymentRepository}, {@link ObligationRepository}) — no conoce si
 * debajo hay Wompi, Mock o cualquier otra.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentApplicationService {

    private final PaymentRepository paymentRepository;
    private final ObligationRepository obligationRepository;
    private final PaymentProvider paymentProvider;
    private final DomainEventPublisher eventPublisher;

    // =========================================================================
    // Caso de uso 1 — GENERAR LINK DE PAGO
    // =========================================================================

    /**
     * Genera un link de pago para una obligación existente y persiste un Payment
     * en estado PENDING con la referencia devuelta por la pasarela.
     *
     * @throws ObligationForPaymentNotFoundException si la obligación no existe.
     * @throws PaymentBusinessException              si la obligación ya está cancelada.
     */
    @Transactional
    public GenerateLinkResponse generateLink(GenerateLinkRequest request) {
        Obligation obligation = obligationRepository.findById(request.obligationId())
                .orElseThrow(() -> new ObligationForPaymentNotFoundException(request.obligationId()));

        if (obligation.getStatus() == Obligation.StatusObligation.CANCELADA) {
            throw new PaymentBusinessException(
                    "La obligación " + obligation.getObligationNumber() + " ya fue cancelada");
        }

        PaymentLinkResponse link = paymentProvider.generateLink(obligation);

        Payment pending = Payment.createPending(
                obligation.getId(),
                obligation.getTotalBalance(),
                link.gatewayReference(),
                request.method()
        );
        Payment saved = paymentRepository.save(pending);

        log.info("Payment PENDING persistido id={} ref={} obligacion={}",
                saved.getId(), saved.getExternalReference(), obligation.getObligationNumber());

        return GenerateLinkResponse.of(saved.getId(), link);
    }

    // =========================================================================
    // Caso de uso 2 — PROCESAR WEBHOOK (ATÓMICO + IDEMPOTENTE)
    // =========================================================================

    /**
     * Procesa la notificación de la pasarela como una única transacción:
     * cualquier excepción provoca rollback completo.
     *
     * <p>Manejo de casos:</p>
     * <ul>
     *   <li>Pago no encontrado por referencia → 404.</li>
     *   <li>Pago ya procesado (CONFIRMED/REJECTED) → idempotente, no-op con WARN.</li>
     *   <li>Estado aprobado → aplica pago sobre la obligación + publica evento.</li>
     *   <li>Estado rechazado → marca Payment como REJECTED.</li>
     * </ul>
     */
    @Transactional
    public PaymentResponse processWebhook(WebhookNotificationRequest notification) {
        // TODO: validar firma HMAC con el secret de la pasarela antes de procesar.

        Payment payment = paymentRepository.findByExternalReference(notification.gatewayReference())
                .orElseThrow(() -> new PaymentNotFoundException(notification.gatewayReference()));

        // Idempotencia: si ya se procesó, devolvemos el estado actual sin reaplicar.
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            log.warn("Webhook duplicado ignorado. paymentId={} status={} ref={}",
                    payment.getId(), payment.getStatus(), payment.getExternalReference());
            return PaymentResponse.fromDomain(payment);
        }

        String normalizedStatus = notification.statusOrEmpty().toUpperCase(Locale.ROOT);
        boolean approved = isApprovedStatus(normalizedStatus);

        if (!approved) {
            payment.reject();
            Payment rejected = paymentRepository.save(payment);
            log.info("Payment RECHAZADO id={} ref={} estadoPasarela={}",
                    rejected.getId(), rejected.getExternalReference(), normalizedStatus);
            return PaymentResponse.fromDomain(rejected);
        }

        return applyApproval(payment);
    }

    /**
     * Aplica la aprobación del pago sobre la obligación en una única unidad
     * de persistencia. Comparte lógica entre webhook y confirmación manual.
     */
    private PaymentResponse applyApproval(Payment payment) {
        Obligation obligation = obligationRepository.findById(payment.getObligationId())
                .orElseThrow(() -> new ObligationForPaymentNotFoundException(payment.getObligationId()));

        obligation.applyPayment(payment.getAmount());
        payment.confirm();

        Payment confirmed = paymentRepository.save(payment);
        obligationRepository.save(obligation);

        eventPublisher.publish(new PaymentConfirmedEvent(
                confirmed.getId(),
                confirmed.getObligationId(),
                confirmed.getAmount(),
                Instant.now()
        ));

        log.info("Payment APROBADO id={} obligacion={} statusObligacion={} saldoTotal={}",
                confirmed.getId(), obligation.getObligationNumber(),
                obligation.getStatus(), obligation.getTotalBalance());

        return PaymentResponse.fromDomain(confirmed);
    }

    private boolean isApprovedStatus(String status) {
        return switch (status) {
            case "APPROVED", "APROBADO", "OK", "SUCCESS", "ACCEPTED" -> true;
            default -> false;
        };
    }

    // =========================================================================
    // Casos de uso CRUD existentes
    // =========================================================================

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        try {
            var existingPayment = paymentRepository.findByExternalReference(request.externalReference());
            if (existingPayment.isPresent()) {
                throw new PaymentBusinessException(
                        "Payment already exists with reference: " + request.externalReference());
            }

            if (request.amount() == null || request.amount().signum() <= 0) {
                throw new PaymentBusinessException("Payment amount must be greater than zero");
            }

            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.method());
            Payment payment = Payment.createPending(
                    request.obligationId(), request.amount(), request.externalReference(), method);
            Payment savedPayment = paymentRepository.save(payment);
            return PaymentResponse.fromDomain(savedPayment);
        } catch (PaymentBusinessException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new PaymentBusinessException("Invalid payment method: " + request.method());
        } catch (Exception e) {
            throw new PaymentBusinessException("Error creating payment", e);
        }
    }

    @Transactional(readOnly = true)
    public PaymentResponse getById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentResponse.fromDomain(payment);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByReference(String externalReference) {
        Payment payment = paymentRepository.findByExternalReference(externalReference)
                .orElseThrow(() -> new PaymentNotFoundException(externalReference));
        return PaymentResponse.fromDomain(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listByObligation(Long obligationId) {
        return paymentRepository.findByObligationId(obligationId).stream()
                .map(PaymentResponse::fromDomain)
                .toList();
    }

    /**
     * Confirmación manual (endpoint interno protegido con JWT). A diferencia
     * del webhook <b>no</b> es idempotente: si el pago no está en PENDING se
     * lanza {@link PaymentBusinessException}. También actualiza la obligación
     * para mantener consistencia atómica.
     */
    @Transactional
    public PaymentResponse confirmPayment(ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findByExternalReference(request.reference())
                .orElseThrow(() -> new PaymentNotFoundException(request.reference()));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentBusinessException("Only payments in PENDING status can be confirmed");
        }

        return applyApproval(payment);
    }

    @Transactional
    public PaymentResponse rejectPayment(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentBusinessException("Only payments in PENDING status can be rejected");
        }

        payment.reject();
        Payment updatedPayment = paymentRepository.save(payment);
        return PaymentResponse.fromDomain(updatedPayment);
    }
}
