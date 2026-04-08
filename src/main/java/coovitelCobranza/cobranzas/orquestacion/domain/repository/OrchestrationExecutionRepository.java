package coovitelCobranza.cobranzas.orquestacion.domain.repository;

import coovitelCobranza.cobranzas.orquestacion.domain.model.OrchestrationExecution;

import java.util.List;
import java.util.Optional;

public interface OrchestrationExecutionRepository {

    OrchestrationExecution save(OrchestrationExecution ejecucion);

    Optional<OrchestrationExecution> findById(Long id);

    List<OrchestrationExecution> findByCaseId(Long casoGestionId);
}

