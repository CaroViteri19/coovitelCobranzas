package coovitelCobranza.cobranzas.payment.domain.repository;

import coovitelCobranza.cobranzas.payment.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment pago);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByExternalReference(String externalReference);

    List<Payment> findByObligationId(Long obligationId);
}

