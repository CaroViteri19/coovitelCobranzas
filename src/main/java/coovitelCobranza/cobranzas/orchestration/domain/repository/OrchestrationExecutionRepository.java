package coovitelCobranza.cobranzas.orchestration.domain.repository;

import coovitelCobranza.cobranzas.orchestration.domain.model.OrchestrationExecution;

import java.util.List;
import java.util.Optional;

public interface OrchestrationExecutionRepository {

    OrchestrationExecution save(OrchestrationExecution ejecucion);

    Optional<OrchestrationExecution> findById(Long id);

    List<OrchestrationExecution> findByCaseId(Long casoGestionId);
}

