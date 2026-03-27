package coovitelCobranza.cobranzas.obligacion.domain.repository;

import coovitelCobranza.cobranzas.obligacion.domain.model.Obligacion;

import java.util.List;
import java.util.Optional;

public interface ObligacionRepository {

    Obligacion save(Obligacion obligacion);

    Optional<Obligacion> findById(Long id);

    Optional<Obligacion> findByNumeroObligacion(String numeroObligacion);

    List<Obligacion> findByClienteId(Long clienteId);
}

