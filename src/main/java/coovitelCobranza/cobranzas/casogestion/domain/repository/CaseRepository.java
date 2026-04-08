package coovitelCobranza.cobranzas.casogestion.domain.repository;

import coovitelCobranza.cobranzas.casogestion.domain.model.Case;

import java.util.List;
import java.util.Optional;

public interface CaseRepository {

    Case save(Case casoGestion);

    Optional<Case> findById(Long id);

    List<Case> findPendientes();
}

