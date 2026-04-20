package coovitelCobranza.cobranzas.orchestration.infrastructure.persistence.repository;

import coovitelCobranza.cobranzas.orchestration.infrastructure.persistence.entity.OrchestrationExecutionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrchestrationExecutionJpaRepository extends JpaRepository<OrchestrationExecutionJpaEntity, Long> {

    List<OrchestrationExecutionJpaEntity> findByCaseId(Long caseId);
}

