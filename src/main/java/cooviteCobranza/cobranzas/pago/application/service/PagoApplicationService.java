package cooviteCobranza.cobranzas.pago.application.service;

import cooviteCobranza.cobranzas.pago.application.dto.ConfirmarPagoRequest;
import cooviteCobranza.cobranzas.pago.application.dto.CrearPagoRequest;
import cooviteCobranza.cobranzas.pago.application.dto.PagoResponse;
import cooviteCobranza.cobranzas.pago.application.exception.PagoBusinessException;
import cooviteCobranza.cobranzas.pago.application.exception.PagoNotFoundException;
import cooviteCobranza.cobranzas.pago.domain.event.PagoConfirmadoEvent;
import cooviteCobranza.cobranzas.pago.domain.model.Pago;
import cooviteCobranza.cobranzas.pago.domain.repository.PagoRepository;
import cooviteCobranza.cobranzas.shared.domain.event.DomainEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
public class PagoApplicationService {

    private final PagoRepository pagoRepository;
    private final DomainEventPublisher eventPublisher;

    public PagoApplicationService(PagoRepository pagoRepository,
                                  DomainEventPublisher eventPublisher) {
        this.pagoRepository = pagoRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PagoResponse crearPago(CrearPagoRequest request) {
        try {
            // Validar que el pago no exista
            var pagoExistente = pagoRepository.findByReferenciaExterna(request.referenciaExterna());
            if (pagoExistente.isPresent()) {
                throw new PagoBusinessException("Pago ya existe con referencia: " + request.referenciaExterna());
            }

            // Validar monto positivo
            if (request.valor() == null || request.valor().signum() <= 0) {
                throw new PagoBusinessException("El valor del pago debe ser mayor a cero");
            }

            // Crear nuevo pago
            Pago.MetodoPago metodo = Pago.MetodoPago.valueOf(request.metodo());
            Pago pago = Pago.crearPendiente(request.obligacionId(), request.valor(), request.referenciaExterna(), metodo);

            Pago pagoGuardado = pagoRepository.save(pago);
            return PagoResponse.fromDomain(pagoGuardado);
        } catch (PagoBusinessException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new PagoBusinessException("Método de pago inválido: " + request.metodo());
        } catch (Exception e) {
            throw new PagoBusinessException("Error al crear pago", e);
        }
    }

    @Transactional(readOnly = true)
    public PagoResponse obtenerPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));
        return PagoResponse.fromDomain(pago);
    }

    @Transactional(readOnly = true)
    public PagoResponse obtenerPorReferencia(String referenciaExterna) {
        Pago pago = pagoRepository.findByReferenciaExterna(referenciaExterna)
                .orElseThrow(() -> new PagoNotFoundException(referenciaExterna));
        return PagoResponse.fromDomain(pago);
    }

    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorObligacion(Long obligacionId) {
        return pagoRepository.findByObligacionId(obligacionId).stream()
                .map(PagoResponse::fromDomain)
                .toList();
    }

    @Transactional
    public PagoResponse confirmarPago(ConfirmarPagoRequest request) {
        Pago pago = pagoRepository.findByReferenciaExterna(request.referencia())
                .orElseThrow(() -> new PagoNotFoundException(request.referencia()));

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            throw new PagoBusinessException("Solo se pueden confirmar pagos en estado PENDIENTE");
        }

        pago.confirmar();
        Pago pagoActualizado = pagoRepository.save(pago);

        eventPublisher.publish(new PagoConfirmadoEvent(
                pagoActualizado.getId(),
                pagoActualizado.getObligacionId(),
                pagoActualizado.getValor(),
                Instant.now()
        ));

        return PagoResponse.fromDomain(pagoActualizado);
    }

    @Transactional
    public PagoResponse rechazarPago(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new PagoNotFoundException(id));

        if (pago.getEstado() != Pago.EstadoPago.PENDIENTE) {
            throw new PagoBusinessException("Solo se pueden rechazar pagos en estado PENDIENTE");
        }

        pago.rechazar();
        Pago pagoActualizado = pagoRepository.save(pago);
        return PagoResponse.fromDomain(pagoActualizado);
    }
}


