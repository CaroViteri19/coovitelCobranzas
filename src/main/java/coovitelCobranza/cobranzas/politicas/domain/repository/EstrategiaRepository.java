package coovitelCobranza.cobranzas.politicas.domain.repository;

import coovitelCobranza.cobranzas.politicas.domain.model.Estrategia;

import java.util.List;
import java.util.Optional;

public interface EstrategiaRepository {

    Estrategia save(Estrategia estrategia);

    Optional<Estrategia> findById(Long id);

    List<Estrategia> findActivas();

    Optional<Estrategia> findByNombre(String nombre);
}

