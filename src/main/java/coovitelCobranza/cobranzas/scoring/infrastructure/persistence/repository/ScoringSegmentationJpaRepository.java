package coovitelCobranza.cobranzas.scoring.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.scoring.infrastructure.persistence.entity.ScoringSegmentationJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoringSegmentationJpaRepository extends JpaRepository<ScoringSegmentationJpaEntity, Long> {

    Optional<ScoringSegmentationJpaEntity> findTopByObligacionIdOrderByCreatedAtDesc(Long obligacionId);

    List<ScoringSegmentationJpaEntity> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
}

