package coovitelCobranza.cobranzas.audit.domain.service;

/**
 * Domain service for registering audit events.
 *
 * <p>Provides two variants of {@code registerEvent}:
 * <ul>
 *   <li>The signature <b>with</b> an explicit {@code correlationId} — useful when
 *       the ID is received from another layer (for example, a distributed domain event).</li>
 *   <li>The signature <b>without</b> {@code correlationId} — automatically retrieves
 *       the current thread ID from {@code AuditContext}. This is the recommended
 *       form for local flows.</li>
 * </ul>
 */
public interface AuditService {

    /**
     * Registers an audit event with an explicit {@code correlationId}.
     */
    void registerEvent(String module,
                       String entityType,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String details,
                       String correlationId);

    /**
     * Registers an audit event, automatically retrieving the current thread
     * {@code correlationId} from {@code AuditContext}. If there is no active
     * context, the event is persisted with {@code correlationId = null}.
     */
    void registerEvent(String module,
                       String entityType,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String details);
}
