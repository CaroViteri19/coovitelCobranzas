package coovitelCobranza.cobranzas.orchestration.application.service;

import coovitelCobranza.cobranzas.interaction.domain.model.Interaction;
import coovitelCobranza.cobranzas.orchestration.application.dto.OrchestrationExecutionResponse;
import coovitelCobranza.cobranzas.orchestration.application.dto.SendOrchestrationRequest;
import coovitelCobranza.cobranzas.orchestration.application.exception.OrchestrationBusinessException;
import coovitelCobranza.cobranzas.orchestration.application.exception.OrchestrationNotFoundException;
import coovitelCobranza.cobranzas.orchestration.domain.model.OrchestrationExecution;
import coovitelCobranza.cobranzas.orchestration.domain.repository.OrchestrationExecutionRepository;
import coovitelCobranza.cobranzas.orchestration.domain.service.ChannelOrchestrator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrchestrationApplicationService {

    private final OrchestrationExecutionRepository repository;
    private final ChannelOrchestrator orchestratorChannels;

    public OrchestrationApplicationService(OrchestrationExecutionRepository repository,
                                           ChannelOrchestrator orchestratorChannels) {
        this.repository = repository;
        this.orchestratorChannels = orchestratorChannels;
    }

    @Transactional
    public OrchestrationExecutionResponse send(SendOrchestrationRequest request) {
        try {
            Interaction.Channel channel = Interaction.Channel.valueOf(request.canal());
            OrchestrationExecution execution = OrchestrationExecution.crear(
                    request.casoGestionId(),
                    request.canal(),
                    request.destino(),
                    request.plantilla()
            );

            orchestratorChannels.send(request.casoGestionId(), channel, request.plantilla(), request.destino());
            execution.marcarEnviado();

            OrchestrationExecution saved = repository.save(execution);
            return OrchestrationExecutionResponse.fromDomain(saved);
        } catch (IllegalArgumentException e) {
            throw new OrchestrationBusinessException("Invalid channel: " + request.canal());
        } catch (Exception e) {
            throw new OrchestrationBusinessException("Error orchestrating send", e);
        }
    }

    @Transactional(readOnly = true)
    public OrchestrationExecutionResponse getById(Long id) {
        OrchestrationExecution execution = repository.findById(id)
                .orElseThrow(() -> new OrchestrationNotFoundException(id));
        return OrchestrationExecutionResponse.fromDomain(execution);
    }

    @Transactional(readOnly = true)
    public List<OrchestrationExecutionResponse> listByCase(Long caseId) {
        return repository.findByCaseId(caseId).stream()
                .map(OrchestrationExecutionResponse::fromDomain)
                .toList();
    }
}
