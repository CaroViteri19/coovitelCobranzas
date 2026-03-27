package cooviteCobranza.cobranzas.politicas.application.service;

import cooviteCobranza.cobranzas.politicas.application.dto.CrearPoliticaRequest;
import cooviteCobranza.cobranzas.politicas.application.dto.PoliticaResponse;
import cooviteCobranza.cobranzas.politicas.application.exception.EstrategiaNotFoundException;
import cooviteCobranza.cobranzas.politicas.application.exception.PoliticaNotFoundException;
import cooviteCobranza.cobranzas.politicas.application.exception.PoliticasBusinessException;
import cooviteCobranza.cobranzas.politicas.domain.model.Politica;
import cooviteCobranza.cobranzas.politicas.domain.repository.EstrategiaRepository;
import cooviteCobranza.cobranzas.politicas.domain.repository.PoliticaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PoliticaApplicationService {

    private final PoliticaRepository politicaRepository;
    private final EstrategiaRepository estrategiaRepository;

    public PoliticaApplicationService(PoliticaRepository politicaRepository,
                                      EstrategiaRepository estrategiaRepository) {
        this.politicaRepository = politicaRepository;
        this.estrategiaRepository = estrategiaRepository;
    }

    @Transactional
    public PoliticaResponse crear(CrearPoliticaRequest request) {
        try {
            // Validar estrategia existe
            estrategiaRepository.findById(request.estrategiaId())
                    .orElseThrow(() -> new EstrategiaNotFoundException(request.estrategiaId()));

            // Crear política
            Politica.TipoCobro tipoCobro = Politica.TipoCobro.valueOf(request.tipoCobro());
            Politica politica = Politica.crear(request.estrategiaId(), tipoCobro, request.accion());
            politica.configurarRangoMora(request.diasMoraMinimo(), request.diasMoraMaximo());

            Politica guardada = politicaRepository.save(politica);
            return PoliticaResponse.fromDomain(guardada);
        } catch (EstrategiaNotFoundException e) {
            throw e;
        } catch (IllegalArgumentException e) {
            throw new PoliticasBusinessException("Tipo de cobro inválido: " + request.tipoCobro());
        } catch (Exception e) {
            throw new PoliticasBusinessException("Error al crear política", e);
        }
    }

    @Transactional(readOnly = true)
    public PoliticaResponse obtenerPorId(Long id) {
        Politica politica = politicaRepository.findById(id)
                .orElseThrow(() -> new PoliticaNotFoundException(id));
        return PoliticaResponse.fromDomain(politica);
    }

    @Transactional(readOnly = true)
    public List<PoliticaResponse> listarPorEstrategia(Long estrategiaId) {
        return politicaRepository.findByEstrategiaId(estrategiaId).stream()
                .map(PoliticaResponse::fromDomain)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PoliticaResponse> listarActivas() {
        return politicaRepository.findActivas().stream()
                .map(PoliticaResponse::fromDomain)
                .toList();
    }

    @Transactional
    public PoliticaResponse activar(Long id) {
        Politica politica = politicaRepository.findById(id)
                .orElseThrow(() -> new PoliticaNotFoundException(id));
        politica.activar();
        Politica actualizada = politicaRepository.save(politica);
        return PoliticaResponse.fromDomain(actualizada);
    }

    @Transactional
    public PoliticaResponse desactivar(Long id) {
        Politica politica = politicaRepository.findById(id)
                .orElseThrow(() -> new PoliticaNotFoundException(id));
        politica.desactivar();
        Politica actualizada = politicaRepository.save(politica);
        return PoliticaResponse.fromDomain(actualizada);
    }
}

