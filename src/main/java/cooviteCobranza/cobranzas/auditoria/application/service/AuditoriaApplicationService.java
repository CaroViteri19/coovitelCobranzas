package cooviteCobranza.cobranzas.auditoria.application.service;

import cooviteCobranza.cobranzas.auditoria.application.dto.AuditoriaEventoResponse;
import cooviteCobranza.cobranzas.auditoria.application.dto.RegistrarAuditoriaRequest;
import cooviteCobranza.cobranzas.auditoria.application.exception.AuditoriaBusinessException;
import cooviteCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;
import cooviteCobranza.cobranzas.auditoria.domain.repository.AuditoriaEventoRepository;
import cooviteCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AuditoriaApplicationService {

    private final AuditoriaService auditoriaService;
    private final AuditoriaEventoRepository repository;

    public AuditoriaApplicationService(AuditoriaService auditoriaService,
                                       AuditoriaEventoRepository repository) {
        this.auditoriaService = auditoriaService;
        this.repository = repository;
    }

    @Transactional
    public void registrar(RegistrarAuditoriaRequest request) {
        try {
            auditoriaService.registrarEvento(
                    request.entidad(),
                    request.entidadId(),
                    request.accion(),
                    request.usuario(),
                    request.detalle()
            );
        } catch (Exception e) {
            throw new AuditoriaBusinessException("Error al registrar auditoría", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listarPorEntidad(String entidad, Long entidadId) {
        List<AuditoriaEvento> eventos = repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc(entidad, entidadId);
        return eventos.stream().map(AuditoriaEventoResponse::fromDomain).toList();
    }
}

