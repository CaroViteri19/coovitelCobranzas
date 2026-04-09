package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casogestion.domain.model.CaseAssignmentTrace;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CaseAssignmentTraceRepository;
import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseAssignmentTraceJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class CaseAssignmentTraceRepositoryImpl implements CaseAssignmentTraceRepository {

    private final CaseAssignmentTraceJpaRepository jpaRepository;

    public CaseAssignmentTraceRepositoryImpl(CaseAssignmentTraceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public CaseAssignmentTrace save(CaseAssignmentTrace trace) {
        CaseAssignmentTraceJpaEntity entity = new CaseAssignmentTraceJpaEntity(
                trace.getId(),
                trace.getCaseId(),
                trace.getAdvisor(),
                trace.getAssignmentSource(),
                trace.getPerformedBy(),
                trace.getPerformedByRole(),
                trace.getCorrelationId(),
                trace.getCreatedAt()
        );
        CaseAssignmentTraceJpaEntity saved = jpaRepository.save(entity);
        return CaseAssignmentTrace.reconstruct(
                saved.getId(),
                saved.getCaseId(),
                saved.getAdvisor(),
                saved.getAssignmentSource(),
                saved.getPerformedBy(),
                saved.getPerformedByRole(),
                saved.getCorrelationId(),
                saved.getCreatedAt()
        );
    }
}

