package coovitelCobranza.cobranzas.policies.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.policies.domain.model.Policy;
import coovitelCobranza.cobranzas.policies.domain.repository.PolicyRepository;
import coovitelCobranza.cobranzas.policies.infrastructure.persistence.entity.PolicyJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class PolicyRepositoryImpl implements PolicyRepository {

    private final PolicyJpaRepository jpaRepository;

    public PolicyRepositoryImpl(PolicyJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Policy save(Policy politica) {
        PolicyJpaEntity entity = toEntity(politica);
        PolicyJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Policy> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Policy> findByStrategyId(Long strategyId) {
        return jpaRepository.findByStrategyId(strategyId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Policy> findActivas() {
        return jpaRepository.findByActivaTrue().stream()
                .map(this::toDomain)
                .toList();
    }

    private PolicyJpaEntity toEntity(Policy politica) {
        return new PolicyJpaEntity(
                politica.getId(),
                politica.getStrategyId(),
                politica.getCollectionType().name(),
                politica.getDiasDelinquencyMinimo(),
                politica.getDiasDelinquencyMaximo(),
                politica.getAction(),
                politica.isActiva(),
                politica.getUpdatedAt()
        );
    }

    private Policy toDomain(PolicyJpaEntity entity) {
        return Policy.reconstruct(
                entity.getId(),
                entity.getStrategyId(),
                Policy.CollectionType.valueOf(entity.getCollectionType()),
                entity.getDiasDelinquencyMinimo(),
                entity.getDiasDelinquencyMaximo(),
                entity.getAction(),
                entity.isActiva(),
                entity.getUpdatedAt()
        );
    }
}

