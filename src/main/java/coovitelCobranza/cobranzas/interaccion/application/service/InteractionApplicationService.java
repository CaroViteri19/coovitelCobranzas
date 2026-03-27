package coovitelCobranza.cobranzas.interaccion.application.service;

import coovitelCobranza.cobranzas.interaccion.application.dto.UpdateInteractionResultRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.CreateInteractionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.InteractionResponse;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteractionBusinessException;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteractionNotFoundException;
import coovitelCobranza.cobranzas.interaccion.domain.event.InteraccionResultadoActualizadoEvent;
import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;
import coovitelCobranza.cobranzas.interaccion.domain.repository.InteraccionRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InteractionApplicationService {

    private final InteraccionRepository interaccionRepository;
    private final DomainEventPublisher eventPublisher;

    public InteractionApplicationService(InteraccionRepository interaccionRepository,
                                         DomainEventPublisher eventPublisher) {
        this.interaccionRepository = interaccionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public InteractionResponse createInteraction(CreateInteractionRequest request) {
        try {
            // Validate channel
            Interaccion.Canal channel = Interaccion.Canal.valueOf(request.channel());

            // Create new interaction
            Interaccion interaccion = Interaccion.crear(request.caseId(), channel, request.template());

            Interaccion savedInteraction = interaccionRepository.save(interaccion);
            return InteractionResponse.fromDomain(savedInteraction);
        } catch (IllegalArgumentException e) {
            throw new InteractionBusinessException("Invalid channel: " + request.channel());
        } catch (Exception e) {
            throw new InteractionBusinessException("Error creating interaction", e);
        }
    }

    @Transactional(readOnly = true)
    public InteractionResponse getById(Long id) {
        Interaccion interaccion = interaccionRepository.findById(id)
                .orElseThrow(() -> new InteractionNotFoundException(id));
        return InteractionResponse.fromDomain(interaccion);
    }

    @Transactional(readOnly = true)
    public List<InteractionResponse> listByCase(Long caseId) {
        return interaccionRepository.findByCasoGestionId(caseId).stream()
                .map(InteractionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public InteractionResponse updateResult(Long id, UpdateInteractionResultRequest request) {
        Interaccion interaccion = interaccionRepository.findById(id)
                .orElseThrow(() -> new InteractionNotFoundException(id));

        try {
            Interaccion.EstadoResultado newResult = Interaccion.EstadoResultado.valueOf(request.result());

            // Update status according to result
            switch (newResult) {
                case ENTREGADA -> interaccion.marcarEntregada();
                case LEIDA -> interaccion.marcarLeida();
                case FALLIDA -> interaccion.marcarFallida();
                default -> {
                    // For other statuses, would need to add methods in the model
                }
            }

            Interaccion updatedInteraction = interaccionRepository.save(interaccion);

            eventPublisher.publish(new InteraccionResultadoActualizadoEvent(
                    updatedInteraction.getId(),
                    updatedInteraction.getCasoGestionId(),
                    updatedInteraction.getResultado().name(),
                    Instant.now()
            ));

            return InteractionResponse.fromDomain(updatedInteraction);
        } catch (IllegalArgumentException e) {
            throw new InteractionBusinessException("Invalid result: " + request.result());
        }
    }
}

