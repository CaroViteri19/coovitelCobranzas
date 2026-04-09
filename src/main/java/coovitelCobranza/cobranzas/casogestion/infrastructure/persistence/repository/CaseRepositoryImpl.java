package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casogestion.domain.model.Case;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseRepository;
import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseJpaEntity;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CaseRepositoryImpl implements CaseRepository {

    private final CaseJpaRepository jpaRepository;

    public CaseRepositoryImpl(CaseJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Case save(Case casoGestion) {
        CaseJpaEntity entity = casoGestionToEntity(casoGestion);
        CaseJpaEntity savedEntity = jpaRepository.save(entity);
        return entityToCase(savedEntity);
    }

    @Override
    public Optional<Case> findById(Long id) {
        return jpaRepository.findById(id).map(this::entityToCase);
    }

    @Override
    public List<Case> findPendientes() {
        return jpaRepository.findPendientes().stream()
                .map(this::entityToCase)
                .toList();
    }

    // Mappers
    private CaseJpaEntity casoGestionToEntity(Case casoGestion) {
        return new CaseJpaEntity(
                casoGestion.getId(),
                casoGestion.getObligationId(),
                casoGestion.getPriority().name(),
                casoGestion.getStatus().name(),
                casoGestion.getAssignedAdvisor(),
                casoGestion.getNextActionAt(),
                casoGestion.getUpdatedAt()
        );
    }

    private Case entityToCase(CaseJpaEntity entity) {
        return Case.reconstruct(
                entity.getId(),
                entity.getObligationId(),
                Case.Priority.valueOf(entity.getPriority()),
                Case.statusFromPersistence(entity.getStatus()),
                entity.getAssignedAdvisor(),
                entity.getNextActionAt(),
                entity.getUpdatedAt()
        );
    }
}

