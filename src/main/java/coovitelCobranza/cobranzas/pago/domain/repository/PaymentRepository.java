package coovitelCobranza.cobranzas.pago.domain.repository;

import coovitelCobranza.cobranzas.pago.domain.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {

    Payment save(Payment pago);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByExternalReference(String externalReference);

    List<Payment> findByObligationId(Long obligationId);
}

