package cooviteCobranza.cobranzas.orquestacion.application.service;

import cooviteCobranza.cobranzas.interaccion.domain.model.Interaccion;
import cooviteCobranza.cobranzas.orquestacion.application.dto.EnviarOrquestacionRequest;
import cooviteCobranza.cobranzas.orquestacion.application.dto.OrquestacionEjecucionResponse;
import cooviteCobranza.cobranzas.orquestacion.application.exception.OrquestacionBusinessException;
import cooviteCobranza.cobranzas.orquestacion.application.exception.OrquestacionNotFoundException;
import cooviteCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;
import cooviteCobranza.cobranzas.orquestacion.domain.repository.OrquestacionEjecucionRepository;
import cooviteCobranza.cobranzas.orquestacion.domain.service.OrquestadorCanales;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrquestacionApplicationService {

    private final OrquestacionEjecucionRepository repository;
    private final OrquestadorCanales orquestadorCanales;

    public OrquestacionApplicationService(OrquestacionEjecucionRepository repository,
                                          OrquestadorCanales orquestadorCanales) {
        this.repository = repository;
        this.orquestadorCanales = orquestadorCanales;
    }

    @Transactional
    public OrquestacionEjecucionResponse enviar(EnviarOrquestacionRequest request) {
        try {
            Interaccion.Canal canal = Interaccion.Canal.valueOf(request.canal());

            OrquestacionEjecucion ejecucion = OrquestacionEjecucion.crear(
                    request.casoGestionId(),
                    request.canal(),
                    request.destino(),
                    request.plantilla()
            );

            orquestadorCanales.enviar(request.casoGestionId(), canal, request.plantilla(), request.destino());
            ejecucion.marcarEnviado();

            OrquestacionEjecucion saved = repository.save(ejecucion);
            return OrquestacionEjecucionResponse.fromDomain(saved);
        } catch (IllegalArgumentException e) {
            throw new OrquestacionBusinessException("Canal inválido: " + request.canal());
        } catch (Exception e) {
            throw new OrquestacionBusinessException("Error al orquestar envío", e);
        }
    }

    @Transactional(readOnly = true)
    public OrquestacionEjecucionResponse obtenerPorId(Long id) {
        OrquestacionEjecucion ejecucion = repository.findById(id)
                .orElseThrow(() -> new OrquestacionNotFoundException(id));
        return OrquestacionEjecucionResponse.fromDomain(ejecucion);
    }

    @Transactional(readOnly = true)
    public List<OrquestacionEjecucionResponse> listarPorCasoGestion(Long casoGestionId) {
        return repository.findByCasoGestionId(casoGestionId).stream()
                .map(OrquestacionEjecucionResponse::fromDomain)
                .toList();
    }
}

