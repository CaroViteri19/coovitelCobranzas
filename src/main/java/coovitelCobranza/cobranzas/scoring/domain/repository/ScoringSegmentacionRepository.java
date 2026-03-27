package coovitelCobranza.cobranzas.scoring.domain.repository;

import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;

import java.util.List;
import java.util.Optional;

public interface ScoringSegmentacionRepository {

    ScoringSegmentacion save(ScoringSegmentacion scoringSegmentacion);

    Optional<ScoringSegmentacion> findById(Long id);

    Optional<ScoringSegmentacion> findTopByObligacionIdOrderByCreatedAtDesc(Long obligacionId);

    List<ScoringSegmentacion> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
}

