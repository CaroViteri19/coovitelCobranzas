package coovitelCobranza.cobranzas.obligacion.domain.repository;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligation;

import java.util.List;
import java.util.Optional;

public interface ObligationRepository {

    Obligation save(Obligation obligacion);

    Optional<Obligation> findById(Long id);

    Optional<Obligation> findByObligationNumber(String obligationNumber);

    List<Obligation> findByCustomerId(Long customerId);
}

