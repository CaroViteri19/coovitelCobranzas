package coovitelCobranza.cobranzas.orquestacion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.orquestacion.domain.model.OrchestrationExecution;
import coovitelCobranza.cobranzas.orquestacion.domain.repository.OrchestrationExecutionRepository;
import coovitelCobranza.cobranzas.orquestacion.infrastructure.persistence.entity.OrchestrationExecutionJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrchestrationExecutionRepositoryImpl implements OrchestrationExecutionRepository {

    private final OrchestrationExecutionJpaRepository jpaRepository;

    public OrchestrationExecutionRepositoryImpl(OrchestrationExecutionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OrchestrationExecution save(OrchestrationExecution ejecucion) {
        OrchestrationExecutionJpaEntity entity = toEntity(ejecucion);
        OrchestrationExecutionJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<OrchestrationExecution> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<OrchestrationExecution> findByCaseId(Long casoGestionId) {
        return jpaRepository.findByCasoGestionId(casoGestionId).stream()
                .map(this::toDomain)
                .toList();
    }

    private OrchestrationExecutionJpaEntity toEntity(OrchestrationExecution ejecucion) {
        return new OrchestrationExecutionJpaEntity(
                ejecucion.getId(),
                ejecucion.getCaseId(),
                ejecucion.getChannel(),
                ejecucion.getDestino(),
                ejecucion.getTemplate(),
                ejecucion.getStatus().name(),
                ejecucion.getCreatedAt()
        );
    }

    private OrchestrationExecution toDomain(OrchestrationExecutionJpaEntity entity) {
        return OrchestrationExecution.reconstruct(
                entity.getId(),
                entity.getCaseId(),
                entity.getChannel(),
                entity.getDestino(),
                entity.getTemplate(),
                OrchestrationExecution.Status.valueOf(entity.getStatus()),
                entity.getCreatedAt()
        );
    }
}

