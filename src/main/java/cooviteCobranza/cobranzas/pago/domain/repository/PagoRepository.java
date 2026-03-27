package cooviteCobranza.cobranzas.pago.domain.repository;

import cooviteCobranza.cobranzas.pago.domain.model.Pago;

import java.util.List;
import java.util.Optional;

public interface PagoRepository {

    Pago save(Pago pago);

    Optional<Pago> findById(Long id);

    Optional<Pago> findByReferenciaExterna(String referenciaExterna);

    List<Pago> findByObligacionId(Long obligacionId);
}

