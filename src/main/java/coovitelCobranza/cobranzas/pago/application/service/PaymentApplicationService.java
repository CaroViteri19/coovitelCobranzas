package coovitelCobranza.cobranzas.pago.application.service;

import coovitelCobranza.cobranzas.pago.application.dto.ConfirmPaymentRequest;
import coovitelCobranza.cobranzas.pago.application.dto.CreatePaymentRequest;
import coovitelCobranza.cobranzas.pago.application.dto.PaymentResponse;
import coovitelCobranza.cobranzas.pago.application.exception.PaymentBusinessException;
import coovitelCobranza.cobranzas.pago.application.exception.PaymentNotFoundException;
import coovitelCobranza.cobranzas.pago.domain.event.PaymentConfirmedEvent;
import coovitelCobranza.cobranzas.pago.domain.model.Payment;
import coovitelCobranza.cobranzas.pago.domain.repository.PaymentRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class PaymentApplicationService {

    private final PaymentRepository paymentRepository;
    private final DomainEventPublisher eventPublisher;

    public PaymentApplicationService(PaymentRepository paymentRepository,
                                     DomainEventPublisher eventPublisher) {
        this.paymentRepository = paymentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PaymentResponse createPayment(CreatePaymentRequest request) {
        try {
            var existingPayment = paymentRepository.findByExternalReference(request.externalReference());
            if (existingPayment.isPresent()) {
                throw new PaymentBusinessException("Payment already exists with reference: " + request.externalReference());
            }

            if (request.amount() == null || request.amount().signum() <= 0) {
                throw new PaymentBusinessException("Payment amount must be greater than zero");
            }

            Payment.PaymentMethod method = Payment.PaymentMethod.valueOf(request.method());
            Payment payment = Payment.createPending(request.obligationId(), request.amount(), request.externalReference(), method);
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

    @Transactional
    public PaymentResponse confirmPayment(ConfirmPaymentRequest request) {
        Payment payment = paymentRepository.findByExternalReference(request.reference())
                .orElseThrow(() -> new PaymentNotFoundException(request.reference()));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new PaymentBusinessException("Only payments in PENDING status can be confirmed");
        }

        payment.confirm();
        Payment updatedPayment = paymentRepository.save(payment);

        eventPublisher.publish(new PaymentConfirmedEvent(
                updatedPayment.getId(),
                updatedPayment.getObligationId(),
                updatedPayment.getAmount(),
                Instant.now()
        ));

        return PaymentResponse.fromDomain(updatedPayment);
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
