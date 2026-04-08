package coovitelCobranza.cobranzas.politicas.domain.repository;

import coovitelCobranza.cobranzas.politicas.domain.model.Policy;

import java.util.List;
import java.util.Optional;

public interface PolicyRepository {

    Policy save(Policy politica);

    Optional<Policy> findById(Long id);

    List<Policy> findByStrategyId(Long strategyId);

    List<Policy> findActivas();
}

