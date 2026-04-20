package coovitelCobranza.cobranzas.obligation.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.obligation.domain.model.Obligation;
import coovitelCobranza.cobranzas.obligation.domain.repository.ObligationRepository;
import coovitelCobranza.cobranzas.obligation.infrastructure.persistence.entity.ObligationJpaEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ObligationRepositoryImpl implements ObligationRepository {

    private final ObligationJpaRepository jpaRepository;

    public ObligationRepositoryImpl(ObligationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Obligation save(Obligation obligacion) {
        ObligationJpaEntity entity = ObligationJpaEntity.fromDomain(obligacion);
        ObligationJpaEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }

    @Override
    public Optional<Obligation> findById(Long id) {
        return jpaRepository.findById(id).map(ObligationJpaEntity::toDomain);
    }

    @Override
    public Optional<Obligation> findByObligationNumber(String obligationNumber) {
        return jpaRepository.findByObligationNumber(obligationNumber).map(ObligationJpaEntity::toDomain);
    }

    @Override
    public List<Obligation> findByCustomerId(Long customerId) {
        return jpaRepository.findByCustomerId(customerId).stream()
                .map(ObligationJpaEntity::toDomain)
                .toList();
    }
}

