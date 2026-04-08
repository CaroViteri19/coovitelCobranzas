package coovitelCobranza.cobranzas.scoring.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.scoring.domain.model.ScoringSegmentation;
import coovitelCobranza.cobranzas.scoring.domain.repository.ScoringSegmentationRepository;
import coovitelCobranza.cobranzas.scoring.infrastructure.persistence.entity.ScoringSegmentationJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ScoringSegmentationRepositoryImpl implements ScoringSegmentationRepository {

    private final ScoringSegmentationJpaRepository jpaRepository;

    public ScoringSegmentationRepositoryImpl(ScoringSegmentationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ScoringSegmentation save(ScoringSegmentation scoringSegmentation) {
        ScoringSegmentationJpaEntity entity = toEntity(scoringSegmentation);
        ScoringSegmentationJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<ScoringSegmentation> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<ScoringSegmentation> findTopByObligationIdOrderByCreatedAtDesc(Long obligacionId) {
        return jpaRepository.findTopByObligacionIdOrderByCreatedAtDesc(obligacionId).map(this::toDomain);
    }

    @Override
    public List<ScoringSegmentation> findByClientIdOrderByCreatedAtDesc(Long clienteId) {
        return jpaRepository.findByClienteIdOrderByCreatedAtDesc(clienteId).stream()
                .map(this::toDomain)
                .toList();
    }

    private ScoringSegmentationJpaEntity toEntity(ScoringSegmentation scoring) {
        return new ScoringSegmentationJpaEntity(
                scoring.getId(),
                scoring.getCustomerId(),
                scoring.getObligationId(),
                scoring.getScore(),
                scoring.getSegment(),
                scoring.getModelVersion(),
                scoring.getMainReason(),
                scoring.getCreatedAt()
        );
    }

    private ScoringSegmentation toDomain(ScoringSegmentationJpaEntity entity) {
        return ScoringSegmentation.reconstruct(
                entity.getId(),
                entity.getCustomerId(),
                entity.getObligationId(),
                entity.getScore(),
                entity.getSegment(),
                entity.getModelVersion(),
                entity.getMainReason(),
                entity.getCreatedAt()
        );
    }
}

