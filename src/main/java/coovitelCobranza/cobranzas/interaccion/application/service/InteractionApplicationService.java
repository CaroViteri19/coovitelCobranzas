package coovitelCobranza.cobranzas.interaccion.application.service;

import coovitelCobranza.cobranzas.interaccion.application.dto.CreateInteractionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.InteractionResponse;
import coovitelCobranza.cobranzas.interaccion.application.dto.UpdateInteractionResultRequest;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteractionBusinessException;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteractionNotFoundException;
import coovitelCobranza.cobranzas.interaccion.domain.event.InteractionResultActualizadoEvent;
import coovitelCobranza.cobranzas.interaccion.domain.model.Interaction;
import coovitelCobranza.cobranzas.interaccion.domain.repository.InteractionRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InteractionApplicationService {

    private final InteractionRepository interactionRepository;
    private final DomainEventPublisher eventPublisher;

    public InteractionApplicationService(InteractionRepository interactionRepository,
                                         DomainEventPublisher eventPublisher) {
        this.interactionRepository = interactionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public InteractionResponse createInteraction(CreateInteractionRequest request) {
        try {
            Interaction.Channel channel = Interaction.Channel.valueOf(request.channel());
            Interaction interaction = Interaction.create(request.caseId(), channel, request.template());
            Interaction savedInteraction = interactionRepository.save(interaction);
            return InteractionResponse.fromDomain(savedInteraction);
        } catch (IllegalArgumentException e) {
            throw new InteractionBusinessException("Invalid channel: " + request.channel());
        } catch (Exception e) {
            throw new InteractionBusinessException("Error creating interaction", e);
        }
    }

    @Transactional(readOnly = true)
    public InteractionResponse getById(Long id) {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new InteractionNotFoundException(id));
        return InteractionResponse.fromDomain(interaction);
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> listByCase(Long caseId) {
        return interactionRepository.findByCaseId(caseId).stream()
                .map(InteractionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public InteractionResponse updateResult(Long id, UpdateInteractionResultRequest request) {
        Interaction interaction = interactionRepository.findById(id)
                .orElseThrow(() -> new InteractionNotFoundException(id));

        try {
            Interaction.ResultStatus newResult = Interaction.ResultStatus.valueOf(request.result());
            switch (newResult) {
                case DELIVERED -> interaction.markDelivered();
                case READ -> interaction.markRead();
                case FAILED -> interaction.markFailed();
                default -> {
                    // keep current result when no action is required
                }
            }

            Interaction updatedInteraction = interactionRepository.save(interaction);
            eventPublisher.publish(new InteractionResultActualizadoEvent(
                    updatedInteraction.getId(),
                    updatedInteraction.getCaseId(),
                    updatedInteraction.getResultStatus().name(),
                    Instant.now()
            ));

            return InteractionResponse.fromDomain(updatedInteraction);
        } catch (IllegalArgumentException e) {
            throw new InteractionBusinessException("Invalid result: " + request.result());
        }
    }
}
