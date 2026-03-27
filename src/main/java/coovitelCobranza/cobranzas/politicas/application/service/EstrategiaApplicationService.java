package coovitelCobranza.cobranzas.politicas.application.service;

import coovitelCobranza.cobranzas.politicas.application.dto.CrearEstrategiaRequest;
import coovitelCobranza.cobranzas.politicas.application.dto.EstrategiaResponse;
import coovitelCobranza.cobranzas.politicas.application.exception.EstrategiaNotFoundException;
import coovitelCobranza.cobranzas.politicas.application.exception.PoliticasBusinessException;
import coovitelCobranza.cobranzas.politicas.domain.model.Estrategia;
import coovitelCobranza.cobranzas.politicas.domain.repository.EstrategiaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EstrategiaApplicationService {

    private final EstrategiaRepository estrategiaRepository;

    public EstrategiaApplicationService(EstrategiaRepository estrategiaRepository) {
        this.estrategiaRepository = estrategiaRepository;
    }

    @Transactional
    public EstrategiaResponse crear(CrearEstrategiaRequest request) {
        try {
            // Validar que no exista con mismo nombre
            var existente = estrategiaRepository.findByNombre(request.nombre());
            if (existente.isPresent()) {
                throw new PoliticasBusinessException("Estrategia ya existe con nombre: " + request.nombre());
            }

            // Crear
            Estrategia estrategia = Estrategia.crear(request.nombre(), request.descripcion());
            estrategia.configurarParametros(
                    request.maxIntentosContacto(),
                    request.diasAntesDeeEscalacion(),
                    request.rolAsignacionEscalada()
            );

            Estrategia guardada = estrategiaRepository.save(estrategia);
            return EstrategiaResponse.fromDomain(guardada);
        } catch (PoliticasBusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new PoliticasBusinessException("Error al crear estrategia", e);
        }
    }

    @Transactional(readOnly = true)
    public EstrategiaResponse obtenerPorId(Long id) {
        Estrategia estrategia = estrategiaRepository.findById(id)
                .orElseThrow(() -> new EstrategiaNotFoundException(id));
        return EstrategiaResponse.fromDomain(estrategia);
    }

    @Transactional(readOnly = true)
    public List<EstrategiaResponse> listarActivas() {
        return estrategiaRepository.findActivas().stream()
                .map(EstrategiaResponse::fromDomain)
                .toList();
    }

    @Transactional
    public EstrategiaResponse activar(Long id) {
        Estrategia estrategia = estrategiaRepository.findById(id)
                .orElseThrow(() -> new EstrategiaNotFoundException(id));
        estrategia.activar();
        Estrategia actualizada = estrategiaRepository.save(estrategia);
        return EstrategiaResponse.fromDomain(actualizada);
    }

    @Transactional
    public EstrategiaResponse desactivar(Long id) {
        Estrategia estrategia = estrategiaRepository.findById(id)
                .orElseThrow(() -> new EstrategiaNotFoundException(id));
        estrategia.desactivar();
        Estrategia actualizada = estrategiaRepository.save(estrategia);
        return EstrategiaResponse.fromDomain(actualizada);
    }
}

