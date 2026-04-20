package coovitelCobranza.cobranzas.interaction.domain.repository;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;

import java.util.List;
import java.util.Optional;

public interface InteractionRepository {

    Interaction save(Interaction interaction);

    Optional<Interaction> findById(Long id);

    List<Interaction> findByCaseId(Long casoGestionId);
}

