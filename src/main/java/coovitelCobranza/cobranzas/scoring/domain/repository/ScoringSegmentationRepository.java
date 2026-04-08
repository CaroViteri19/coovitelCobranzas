package coovitelCobranza.cobranzas.scoring.domain.repository;

import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentation;

import java.util.List;
import java.util.Optional;

public interface ScoringSegmentationRepository {

    ScoringSegmentation save(ScoringSegmentation scoringSegmentation);

    Optional<ScoringSegmentation> findById(Long id);

    Optional<ScoringSegmentation> findTopByObligationIdOrderByCreatedAtDesc(Long obligacionId);

    List<ScoringSegmentation> findByClientIdOrderByCreatedAtDesc(Long clienteId);
}

