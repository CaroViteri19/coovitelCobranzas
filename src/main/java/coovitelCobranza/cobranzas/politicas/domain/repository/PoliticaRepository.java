package coovitelCobranza.cobranzas.politicas.domain.repository;

import coovitelCobranza.cobranzas.politicas.domain.model.Politica;

import java.util.List;
import java.util.Optional;

public interface PoliticaRepository {

    Politica save(Politica politica);

    Optional<Politica> findById(Long id);

    List<Politica> findByEstrategiaId(Long estrategiaId);

    List<Politica> findActivas();
}

