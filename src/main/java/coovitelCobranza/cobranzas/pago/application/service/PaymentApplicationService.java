package coovitelCobranza.cobranzas.pago.application.service;

import coovitelCobranza.cobranzas.pago.application.dto.ConfirmPaymentRequest;
import coovitelCobranza.cobranzas.pago.application.dto.CreatePaymentRequest;
import coovitelCobranza.cobranzas.pago.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.pago.application.exception.PaymentBusinessException;
import coovitelCobranza.cobranzas.pago.application.exception.PaymentNotFoundException;
import coovitelCobranza.cobranzas.pago.domain.event.PagoConfirmadoEvent;
import coovitelCobranza.cobranzas.pago.domain.model.Pago;
import coovitelCobranza.cobranzas.pago.domain.repository.PagoRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PaymentApplicationService {

    private final PagoRepository pagoRepository;
    private final DomainEventPublisher eventPublisher;

    public PaymentApplicationService(PagoRepository pagoRepository,
                                  DomainEventPublisher eventPublisher) {
        this.pagoRepository = pagoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        try {
            // Validate payment doesn't already exist
            var existingPayment = pagoRepository.findByReferenciaExterna(request.externalReference());
            if (existingPayment.isPresent()) {
                throw new PaymentBusinessException("Payment already exists with reference: " + request.externalReference());
            }

            // Validate positive amount
            if (request.amount() == null || request.amount().signum() <= 0) {
                throw new PaymentBusinessException("Payment amount must be greater than zero");
            }

            // Create new payment
            Pago.MetodoPago method = Pago.MetodoPago.valueOf(request.method());
            Pago pago = Pago.crearPendiente(request.obligationId(), request.amount(), request.externalReference(), method);

            Pago savedPayment = pagoRepository.save(pago);
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
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));
        return PaymentResponse.fromDomain(pago);
    }

    @Transactional(readOnly = true)
    public PaymentResponse getByReference(String externalReference) {
        Pago pago = pagoRepository.findByReferenciaExterna(externalReference)
                .orElseThrow(() -> new PaymentNotFoundException(externalReference));
        return PaymentResponse.fromDomain(pago);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listByObligation(Long obligationId) {
        return pagoRepository.findByObligacionId(obligationId).stream()
                .map(PaymentResponse::fromDomain)
                .toList();
    }

    @Transactional
    public PaymentResponse confirmPayment(ConfirmPaymentRequest request) {
        Pago pago = pagoRepository.findByReferenciaExterna(request.reference())
                .orElseThrow(() -> new PaymentNotFoundException(request.reference()));

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            throw new PaymentBusinessException("Only payments in PENDING status can be confirmed");
        }

        pago.confirmar();
        Pago updatedPayment = pagoRepository.save(pago);

        eventPublisher.publish(new PagoConfirmadoEvent(
                updatedPayment.getId(),
                updatedPayment.getObligacionId(),
                updatedPayment.getValor(),
                Instant.now()
        ));

        return PaymentResponse.fromDomain(updatedPayment);
    }

    @Transactional
    public PaymentResponse rejectPayment(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException(id));

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            throw new PaymentBusinessException("Only payments in PENDING status can be rejected");
        }

        pago.rechazar();
        Pago updatedPayment = pagoRepository.save(pago);
        return PaymentResponse.fromDomain(updatedPayment);
    }
}

