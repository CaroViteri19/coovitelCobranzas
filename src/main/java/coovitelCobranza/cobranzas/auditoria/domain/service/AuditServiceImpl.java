package coovitelCobranza.cobranzas.auditoria.domain.service;

import coovitelCobranza.cobranzas.auditoria.domain.model.AuditEvent;
import coovitelCobranza.cobranzas.auditoria.domain.repository.AuditEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementación del servicio de auditoría.
 *
 * <p>Cada llamada a {@link #registerEvent} corre en su propia transacción
 * ({@code REQUIRES_NEW}) de forma independiente a la transacción del caller.
 * Esto garantiza que los eventos de auditoría (incluyendo los de fallo)
 * se persistan aunque la transacción principal sea revertida.
 */
@Component
public class AuditServiceImpl implements AuditService {

    private final AuditEventRepository repository;

    public AuditServiceImpl(AuditEventRepository repository) {
        this.repository = repository;
    }

    /**
     * Registra un evento de auditoría en una transacción independiente.
     *
     * <p>Al usar {@code Propagation.REQUIRES_NEW}, este método suspende la
     * transacción activa del caller, abre una nueva transacción, persiste el
     * evento y la confirma (commit) antes de retornar el control al caller.
     * Si el caller luego hace rollback, el evento de auditoría ya quedó guardado.
     *
     * @param module      módulo del sistema que genera el evento (ej. "INTEGRATION")
     * @param entity      entidad afectada (ej. "CARGA_MASIVA", "USUARIO")
     * @param entityId    ID de la entidad (puede ser null cuando no aplica)
     * @param action      acción realizada (ej. "UPLOAD_STARTED", "UPLOAD_FAILED")
     * @param user        usuario que ejecuta la acción
     * @param userRole    rol del usuario
     * @param source      origen de la petición (ej. "WEB", "API")
     * @param details     descripción detallada del evento (máx 1000 caracteres)
     * @param correlationId ID de correlación para trazar flujos distribuidos
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void registerEvent(String module,
                              String entity,
                              Long entityId,
                              String action,
                              String user,
                              String userRole,
                              String source,
                              String details,
                              String correlationId) {
        // 0L como centinela cuando no hay entidad conocida (ej: login fallido de usuario inexistente)
        Long safeEntityId = entityId != null ? entityId : 0L;

        AuditEvent event = AuditEvent.create(
                entity,
                safeEntityId,
                action,
                user,
                userRole,
                source,
                module,
                correlationId,
                details
        );
        repository.save(event);
    }
}
