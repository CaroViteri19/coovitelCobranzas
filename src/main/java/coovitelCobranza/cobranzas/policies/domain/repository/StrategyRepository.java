package coovitelCobranza.cobranzas.policies.domain.repository;

import coovitelCobranza.cobranzas.policies.domain.model.Strategy;

import java.util.List;
import java.util.Optional;

public interface StrategyRepository {

    Strategy save(Strategy estrategia);

    Optional<Strategy> findById(Long id);

    List<Strategy> findActivas();

    Optional<Strategy> findByNombre(String name);
}

