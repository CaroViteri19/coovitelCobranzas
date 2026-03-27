package cooviteCobranza.cobranzas.casogestion.domain.repository;

import cooviteCobranza.cobranzas.casogestion.domain.model.CasoGestion;

import java.util.List;
import java.util.Optional;

public interface CasoGestionRepository {

    CasoGestion save(CasoGestion casoGestion);

    Optional<CasoGestion> findById(Long id);

    List<CasoGestion> findPendientes();
}

