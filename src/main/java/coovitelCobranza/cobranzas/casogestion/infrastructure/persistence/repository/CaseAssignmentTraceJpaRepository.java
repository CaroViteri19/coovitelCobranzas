package coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.casogestion.infrastructure.persistence.entity.CaseAssignmentTraceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CaseAssignmentTraceJpaRepository extends JpaRepository<CaseAssignmentTraceJpaEntity, Long> {
}

