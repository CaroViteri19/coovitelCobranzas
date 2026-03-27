package coovitelCobranza.cobranzas.casogestion.application.service;

import coovitelCobranza.cobranzas.casogestion.application.dto.AsignarAsesorRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.CasoGestionResponse;
import coovitelCobranza.cobranzas.casogestion.application.dto.CrearCasoGestionRequest;
import coovitelCobranza.cobranzas.casogestion.application.dto.ProgramarAccionRequest;
import coovitelCobranza.cobranzas.casogestion.application.exception.CasoGestionBusinessException;
import coovitelCobranza.cobranzas.casogestion.application.exception.CasoGestionNotFoundException;
import coovitelCobranza.cobranzas.casogestion.domain.model.CasoGestion;
import coovitelCobranza.cobranzas.casogestion.domain.repository.CasoGestionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CasoGestionApplicationService {

    private final CasoGestionRepository casoGestionRepository;

    public CasoGestionApplicationService(CasoGestionRepository casoGestionRepository) {
        this.casoGestionRepository = casoGestionRepository;
    }

    @Transactional
    public CasoGestionResponse crearCaso(CrearCasoGestionRequest request) {
        try {
            // Validar prioridad
            CasoGestion.Prioridad prioridad = CasoGestion.Prioridad.valueOf(request.prioridad());

            // Crear nuevo caso
            CasoGestion casoGestion = CasoGestion.crear(request.obligacionId(), prioridad);

            CasoGestion casoGuardado = casoGestionRepository.save(casoGestion);
            return CasoGestionResponse.fromDomain(casoGuardado);
        } catch (IllegalArgumentException e) {
            throw new CasoGestionBusinessException("Prioridad inválida: " + request.prioridad());
        } catch (Exception e) {
            throw new CasoGestionBusinessException("Error al crear caso de gestión", e);
        }
    }

    @Transactional(readOnly = true)
    public CasoGestionResponse obtenerPorId(Long id) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CasoGestionNotFoundException(id));
        return CasoGestionResponse.fromDomain(casoGestion);
    }

    @Transactional(readOnly = true)
    public List<CasoGestionResponse> listarPendientes() {
        return casoGestionRepository.findPendientes().stream()
                .map(CasoGestionResponse::fromDomain)
                .toList();
    }

    @Transactional
    public CasoGestionResponse asignarAsesor(Long id, AsignarAsesorRequest request) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CasoGestionNotFoundException(id));

        try {
            casoGestion.asignarAsesor(request.asesor());
            CasoGestion casoActualizado = casoGestionRepository.save(casoGestion);
            return CasoGestionResponse.fromDomain(casoActualizado);
        } catch (IllegalArgumentException e) {
            throw new CasoGestionBusinessException(e.getMessage());
        }
    }

    @Transactional
    public CasoGestionResponse programarAccion(Long id, ProgramarAccionRequest request) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CasoGestionNotFoundException(id));

        try {
            casoGestion.programarSiguienteAccion(request.fechaHora());
            CasoGestion casoActualizado = casoGestionRepository.save(casoGestion);
            return CasoGestionResponse.fromDomain(casoActualizado);
        } catch (NullPointerException e) {
            throw new CasoGestionBusinessException("Fecha y hora son requeridas");
        }
    }

    @Transactional
    public CasoGestionResponse cerrarCaso(Long id) {
        CasoGestion casoGestion = casoGestionRepository.findById(id)
                .orElseThrow(() -> new CasoGestionNotFoundException(id));

        casoGestion.cerrar();
        CasoGestion casoActualizado = casoGestionRepository.save(casoGestion);
        return CasoGestionResponse.fromDomain(casoActualizado);
    }
}

