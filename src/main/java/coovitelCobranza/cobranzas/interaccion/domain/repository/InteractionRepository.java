package coovitelCobranza.cobranzas.interaccion.domain.repository;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaction;

import java.util.List;
import java.util.Optional;

public interface InteractionRepository {

    Interaction save(Interaction interaction);

    Optional<Interaction> findById(Long id);

    List<Interaction> findByCaseId(Long casoGestionId);
}

