package coovitelCobranza.cobranzas.pago.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.pago.domain.model.Payment;
import coovitelCobranza.cobranzas.pago.domain.repository.PaymentRepository;
import coovitelCobranza.cobranzas.pago.infrastructure.persistence.entity.PaymentJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJpaRepository jpaRepository;

    public PaymentRepositoryImpl(PaymentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = paymentToEntity(payment);
        PaymentJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToPayment(savedEntity);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToPayment);
    }

    @Override
    public Optional<Payment> findByExternalReference(String externalReference) {
        return jpaRepository.findByExternalReference(externalReference).map(this::entityToPayment);
    }

    @Override
    public List<Payment> findByObligationId(Long obligationId) {
        return jpaRepository.findByObligationId(obligationId).stream()
                .map(this::entityToPayment)
                .toList();
    }

    private PaymentJpaEntity paymentToEntity(Payment payment) {
        return new PaymentJpaEntity(
                payment.getId(),
                payment.getObligationId(),
                payment.getAmount(),
                payment.getExternalReference(),
                payment.getMethod().name(),
                payment.getStatus().name(),
                payment.getConfirmedAt(),
                payment.getCreatedAt()
        );
    }

    private Payment entityToPayment(PaymentJpaEntity entity) {
        return Payment.reconstruct(
                entity.getId(),
                entity.getObligationId(),
                entity.getAmount(),
                entity.getExternalReference(),
                Payment.PaymentMethod.valueOf(entity.getMethod()),
                Payment.PaymentStatus.valueOf(entity.getStatus()),
                entity.getConfirmedAt(),
                entity.getCreatedAt()
        );
    }
}
