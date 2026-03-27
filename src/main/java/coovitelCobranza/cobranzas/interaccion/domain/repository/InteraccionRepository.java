package coovitelCobranza.cobranzas.interaccion.domain.repository;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;

import java.util.List;
import java.util.Optional;

public interface InteraccionRepository {

    Interaccion save(Interaccion interaccion);

    Optional<Interaccion> findById(Long id);

    List<Interaccion> findByCasoGestionId(Long casoGestionId);
}

