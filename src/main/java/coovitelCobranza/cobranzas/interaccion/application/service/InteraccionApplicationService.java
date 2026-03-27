package coovitelCobranza.cobranzas.interaccion.application.service;

import coovitelCobranza.cobranzas.interaccion.application.dto.ActualizarResultadoInteraccionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.CrearInteraccionRequest;
import coovitelCobranza.cobranzas.interaccion.application.dto.InteraccionResponse;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteraccionBusinessException;
import coovitelCobranza.cobranzas.interaccion.application.exception.InteraccionNotFoundException;
import coovitelCobranza.cobranzas.interaccion.domain.event.InteraccionResultadoActualizadoEvent;
import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;
import coovitelCobranza.cobranzas.interaccion.domain.repository.InteraccionRepository;
import coovitelCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class InteraccionApplicationService {

    private final InteraccionRepository interaccionRepository;
    private final DomainEventPublisher eventPublisher;

    public InteraccionApplicationService(InteraccionRepository interaccionRepository,
                                         DomainEventPublisher eventPublisher) {
        this.interaccionRepository = interaccionRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public InteraccionResponse crearInteraccion(CrearInteraccionRequest request) {
        try {
            // Validar canal
            Interaccion.Canal canal = Interaccion.Canal.valueOf(request.canal());

            // Crear nueva interacción
            Interaccion interaccion = Interaccion.crear(request.casoGestionId(), canal, request.plantilla());

            Interaccion interaccionGuardada = interaccionRepository.save(interaccion);
            return InteraccionResponse.fromDomain(interaccionGuardada);
        } catch (IllegalArgumentException e) {
            throw new InteraccionBusinessException("Canal inválido: " + request.canal());
        } catch (Exception e) {
            throw new InteraccionBusinessException("Error al crear interacción", e);
        }
    }

    @Transactional(readOnly = true)
    public InteraccionResponse obtenerPorId(Long id) {
        Interaccion interaccion = interaccionRepository.findById(id)
                .orElseThrow(() -> new InteraccionNotFoundException(id));
        return InteraccionResponse.fromDomain(interaccion);
    }

    @Transactional(readOnly = true)
    public List<InteraccionResponse> listarPorCasoGestion(Long casoGestionId) {
        return interaccionRepository.findByCasoGestionId(casoGestionId).stream()
                .map(InteraccionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public InteraccionResponse actualizarResultado(Long id, ActualizarResultadoInteraccionRequest request) {
        Interaccion interaccion = interaccionRepository.findById(id)
                .orElseThrow(() -> new InteraccionNotFoundException(id));

        try {
            Interaccion.EstadoResultado nuevoResultado = Interaccion.EstadoResultado.valueOf(request.resultado());

            // Actualizar estado según el resultado
            switch (nuevoResultado) {
                case ENTREGADA -> interaccion.marcarEntregada();
                case LEIDA -> interaccion.marcarLeida();
                case FALLIDA -> interaccion.marcarFallida();
                default -> {
                    // Para otros estados, sería necesario agregar métodos en el modelo
                }
            }

            Interaccion interaccionActualizada = interaccionRepository.save(interaccion);

            eventPublisher.publish(new InteraccionResultadoActualizadoEvent(
                    interaccionActualizada.getId(),
                    interaccionActualizada.getCasoGestionId(),
                    interaccionActualizada.getResultado().name(),
                    Instant.now()
            ));

            return InteraccionResponse.fromDomain(interaccionActualizada);
        } catch (IllegalArgumentException e) {
            throw new InteraccionBusinessException("Resultado inválido: " + request.resultado());
        }
    }
}


