package coovitelCobranza.cobranzas.auditoria.application.service;

import coovitelCobranza.cobranzas.auditoria.application.dto.AuditEventResponse;
import coovitelCobranza.cobranzas.auditoria.application.dto.AuditoriaEventoResponse;
import coovitelCobranza.cobranzas.auditoria.application.dto.RegisterAuditRequest;
import coovitelCobranza.cobranzas.auditoria.application.dto.RegistrarAuditoriaRequest;
import coovitelCobranza.cobranzas.auditoria.application.exception.AuditoriaBusinessException;
import coovitelCobranza.cobranzas.auditoria.domain.model.AuditoriaEvento;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditoriaEventoRepository;
import coovitelCobranza.cobranzas.auditoria.domain.service.AuditoriaService;
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
    public void register(RegisterAuditRequest request) {
        try {
            auditoriaService.registrarEvento(
                    request.entityType(),
                    request.entityId(),
                    request.action(),
                    request.user(),
                    request.details()
            );
        } catch (Exception e) {
            throw new AuditoriaBusinessException("Error registering audit event", e);
        }
    }

    @Transactional(readOnly = true)
    public List<AuditEventResponse> listByEntity(String entityType, Long entityId) {
        List<AuditoriaEvento> eventos = repository.findByEntidadAndEntidadIdOrderByCreatedAtDesc(entityType, entityId);
        return eventos.stream().map(AuditEventResponse::fromDomain).toList();
    }

    // Backward-compatible wrappers during migration to English API.
    @Deprecated
    @Transactional
    public void registrar(RegistrarAuditoriaRequest request) {
        register(new RegisterAuditRequest(
                request.entidad(),
                request.entidadId(),
                request.accion(),
                request.usuario(),
                request.detalle()
        ));
    }

    @Deprecated
    @Transactional(readOnly = true)
    public List<AuditoriaEventoResponse> listarPorEntidad(String entidad, Long entidadId) {
        return listByEntity(entidad, entidadId).stream()
                .map(event -> new AuditoriaEventoResponse(
                        event.id(),
                        event.entityType(),
                        event.entityId(),
                        event.action(),
                        event.user(),
                        event.details(),
                        event.createdAt()
                ))
                .toList();
    }
}

