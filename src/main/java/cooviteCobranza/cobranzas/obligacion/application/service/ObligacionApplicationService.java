package cooviteCobranza.cobranzas.obligacion.application.service;

import cooviteCobranza.cobranzas.obligacion.application.dto.ObligacionResponse;
import cooviteCobranza.cobranzas.obligacion.application.exception.ObligacionBusinessException;
import cooviteCobranza.cobranzas.obligacion.application.exception.ObligacionNotFoundException;
import cooviteCobranza.cobranzas.obligacion.domain.model.Obligacion;
import cooviteCobranza.cobranzas.obligacion.domain.repository.ObligacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ObligacionApplicationService {

    private final ObligacionRepository obligacionRepository;

    public ObligacionApplicationService(ObligacionRepository obligacionRepository) {
        this.obligacionRepository = obligacionRepository;
    }

    @Transactional(readOnly = true)
    public ObligacionResponse obtenerPorId(Long id) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException(id));
        return ObligacionResponse.fromDomain(obligacion);
    }

    @Transactional(readOnly = true)
    public ObligacionResponse obtenerPorNumero(String numeroObligacion) {
        Obligacion obligacion = obligacionRepository.findByNumeroObligacion(numeroObligacion)
                .orElseThrow(() -> new ObligacionNotFoundException(numeroObligacion));
        return ObligacionResponse.fromDomain(obligacion);
    }

    @Transactional(readOnly = true)
    public List<ObligacionResponse> listarPorCliente(Long clienteId) {
        return obligacionRepository.findByClienteId(clienteId).stream()
                .map(ObligacionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public ObligacionResponse registrarMora(Long id, int diasMora, BigDecimal saldoVencido) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException(id));

        try {
            obligacion.registrarMora(diasMora, saldoVencido);
        } catch (IllegalArgumentException exception) {
            throw new ObligacionBusinessException(exception.getMessage());
        }

        Obligacion saved = obligacionRepository.save(obligacion);
        return ObligacionResponse.fromDomain(saved);
    }

    @Transactional
    public ObligacionResponse aplicarPago(Long id, BigDecimal valorPago) {
        Obligacion obligacion = obligacionRepository.findById(id)
                .orElseThrow(() -> new ObligacionNotFoundException(id));

        try {
            obligacion.aplicarPago(valorPago);
        } catch (IllegalArgumentException exception) {
            throw new ObligacionBusinessException(exception.getMessage());
        }

        Obligacion saved = obligacionRepository.save(obligacion);
        return ObligacionResponse.fromDomain(saved);
    }
}

