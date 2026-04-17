package coovitelCobranza.cobranzas.auditoria.domain.service;

/**
 * Servicio de dominio para el registro de eventos de auditoría.
 *
 * <p>Provee dos variantes de {@code registerEvent}:
 * <ul>
 *   <li>La firma <b>con</b> {@code correlationId} explícito — útil cuando el ID
 *       se recibe desde otra capa (p.ej. un evento de dominio distribuido).</li>
 *   <li>La firma <b>sin</b> {@code correlationId} — recupera automáticamente el
 *       ID del hilo actual desde {@code AuditContext}. Es la forma recomendada
 *       para flujos locales (ej. {@code procesarCarga}).</li>
 * </ul>
 */
public interface AuditService {

    /**
     * Registra un evento de auditoría con un {@code correlationId} explícito.
     */
    void registerEvent(String module,
                       String entity,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String details,
                       String correlationId);

    /**
     * Registra un evento de auditoría recuperando automáticamente el
     * {@code correlationId} del contexto del hilo actual
     * ({@code AuditContext}). Si no hay contexto activo, el evento se persiste
     * con {@code correlationId = null}.
     */
    void registerEvent(String module,
                       String entity,
                       Long entityId,
                       String action,
                       String user,
                       String userRole,
                       String source,
                       String details);
}
/*SELECT id, accion, entidad, correlation_id, created_at
FROM auditoria_eventos
ORDER BY id DESC
LIMIT 10;*/
