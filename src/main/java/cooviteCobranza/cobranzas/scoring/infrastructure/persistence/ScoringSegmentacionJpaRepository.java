package cooviteCobranza.cobranzas.scoring.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScoringSegmentacionJpaRepository extends JpaRepository<ScoringSegmentacionJpaEntity, Long> {

    Optional<ScoringSegmentacionJpaEntity> findTopByObligacionIdOrderByCreatedAtDesc(Long obligacionId);

    List<ScoringSegmentacionJpaEntity> findByClienteIdOrderByCreatedAtDesc(Long clienteId);
}

