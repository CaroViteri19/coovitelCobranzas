package cooviteCobranza.cobranzas.scoring.infrastructure.persistence;

import cooviteCobranza.cobranzas.scoring.domain.model.ScoringSegmentacion;
import cooviteCobranza.cobranzas.scoring.domain.repository.ScoringSegmentacionRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScoringSegmentacionRepositoryImpl implements ScoringSegmentacionRepository {

    private final ScoringSegmentacionJpaRepository jpaRepository;

    public ScoringSegmentacionRepositoryImpl(ScoringSegmentacionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScoringSegmentacion save(ScoringSegmentacion scoringSegmentacion) {
        ScoringSegmentacionJpaEntity entity = toEntity(scoringSegmentacion);
        ScoringSegmentacionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ScoringSegmentacion> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ScoringSegmentacion> findTopByObligacionIdOrderByCreatedAtDesc(Long obligacionId) {
        return jpaRepository.findTopByObligacionIdOrderByCreatedAtDesc(obligacionId).map(this::toDomain);
    }

    @Override
    public List<ScoringSegmentacion> findByClienteIdOrderByCreatedAtDesc(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    private ScoringSegmentacionJpaEntity toEntity(ScoringSegmentacion scoring) {
        return new ScoringSegmentacionJpaEntity(
                scoring.getId(),
                scoring.getClienteId(),
                scoring.getObligacionId(),
                scoring.getScore(),
                scoring.getSegmento(),
                scoring.getVersionModelo(),
                scoring.getRazonPrincipal(),
                scoring.getCreatedAt()
        );
    }

    private ScoringSegmentacion toDomain(ScoringSegmentacionJpaEntity entity) {
        return ScoringSegmentacion.reconstruir(
                entity.getId(),
                entity.getClienteId(),
                entity.getObligacionId(),
                entity.getScore(),
                entity.getSegmento(),
                entity.getVersionModelo(),
                entity.getRazonPrincipal(),
                entity.getCreatedAt()
        );
    }
}

