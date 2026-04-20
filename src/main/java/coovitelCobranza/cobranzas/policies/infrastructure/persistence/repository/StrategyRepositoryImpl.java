package coovitelCobranza.cobranzas.policies.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.policies.domain.model.Strategy;
import coovitelCobranza.cobranzas.policies.domain.repository.StrategyRepository;
import coovitelCobranza.cobranzas.policies.infrastructure.persistence.entity.StrategyJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class StrategyRepositoryImpl implements StrategyRepository {

    private final StrategyJpaRepository jpaRepository;

    public StrategyRepositoryImpl(StrategyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Strategy save(Strategy estrategia) {
        StrategyJpaEntity entity = toEntity(estrategia);
        StrategyJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Strategy> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Strategy> findActivas() {
        return jpaRepository.findByActivaTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Strategy> findByNombre(String name) {
        return jpaRepository.findByName(name).map(this::toDomain);
    }

    private StrategyJpaEntity toEntity(Strategy estrategia) {
        return new StrategyJpaEntity(
                estrategia.getId(),
                estrategia.getNombre(),
                estrategia.getDescripcion(),
                estrategia.isActiva(),
                estrategia.getMaxIntentosContact(),
                estrategia.getDiasAntesDeeEscalacion(),
                estrategia.getRolAsignacionEscalada(),
                estrategia.getUpdatedAt()
        );
    }

    private Strategy toDomain(StrategyJpaEntity entity) {
        return Strategy.reconstruct(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion(),
                entity.isActiva(),
                entity.getMaxIntentosContact(),
                entity.getDiasAntesDeeEscalacion(),
                entity.getRolAsignacionEscalada(),
                entity.getUpdatedAt()
        );
    }
}

