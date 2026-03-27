package coovitelCobranza.cobranzas.orquestacion.application.service;

import coovitelCobranza.cobranzas.interaccion.domain.model.Interaccion;
import coovitelCobranza.cobranzas.orquestacion.application.dto.EnviarOrquestacionRequest;
import coovitelCobranza.cobranzas.orquestacion.application.dto.OrquestacionEjecucionResponse;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrquestacionBusinessException;
import coovitelCobranza.cobranzas.orquestacion.application.exception.OrquestacionNotFoundException;
import coovitelCobranza.cobranzas.orquestacion.domain.model.OrquestacionEjecucion;
import coovitelCobranza.cobranzas.orquestacion.domain.repository.OrquestacionEjecucionRepository;
import coovitelCobranza.cobranzas.orquestacion.domain.service.OrquestadorCanales;
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

