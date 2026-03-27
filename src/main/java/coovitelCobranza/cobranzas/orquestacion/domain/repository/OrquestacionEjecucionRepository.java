package coovitelCobranza.cobranzas.orquestacion.domain.repository;

import coovitelCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;

import java.util.List;
import java.util.Optional;

public interface OrquestacionEjecucionRepository {

    OrquestacionEjecucion save(OrquestacionEjecucion ejecucion);

    Optional<OrquestacionEjecucion> findById(Long id);

    List<OrquestacionEjecucion> findByCasoGestionId(Long casoGestionId);
}

