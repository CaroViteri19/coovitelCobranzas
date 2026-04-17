package coovitelCobranza.cobranzas.auditoria.domain.context;

import java.util.UUID;

/**
 * Contexto de auditoría basado en {@link ThreadLocal} que propaga un
 * identificador de correlación (Correlation ID) a lo largo del ciclo de vida
 * del hilo actual.
 *
 * <p>El ID se genera al inicio de un proceso lógico (por ejemplo,
 * {@code procesarCarga}) y es consultado automáticamente por
 * {@code AuditService} al registrar cualquier evento. De esta forma, todos los
 * eventos emitidos dentro del mismo hilo comparten el mismo UUID y pueden
 * filtrarse por ese ID en la columna {@code id_auditoria} de la tabla
 * {@code auditoria_eventos}.
 *
 * <p><b>Ciclo de vida recomendado:</b>
 * <pre>{@code
 * AuditContext.startNewContext();
 * try {
 *     // ... lógica de negocio que registra eventos de auditoría ...
 * } finally {
 *     AuditContext.clear();
 * }
 * }</pre>
 *
 * <p><b>IMPORTANTE:</b> {@link #clear()} debe invocarse siempre en un bloque
 * {@code finally}. Omitirlo provoca fugas de memoria en servidores con pool
 * de hilos (Tomcat reutiliza los hilos entre peticiones).
 */
public final class AuditContext {

    private static final ThreadLocal<UUID> CORRELATION_ID = new ThreadLocal<>();

    private AuditContext() {
        // Clase de utilidad: no instanciable.
    }

    /**
     * Genera un nuevo UUID aleatorio y lo almacena en el hilo actual como
     * ID de correlación. Sobrescribe cualquier valor previo.
     *
     * @return el UUID recién generado
     */
    public static UUID startNewContext() {
        UUID id = UUID.randomUUID();
        CORRELATION_ID.set(id);
        return id;
    }

    /**
     * Establece manualmente el ID de correlación del hilo actual. Útil para
     * propagar un ID recibido desde otra capa (p.ej. cabecera HTTP).
     */
    public static void setCorrelationId(UUID id) {
        CORRELATION_ID.set(id);
    }

    /**
     * Devuelve el UUID asociado al hilo actual, o {@code null} si no hay
     * contexto activo.
     */
    public static UUID getCorrelationId() {
        return CORRELATION_ID.get();
    }

    /**
     * Devuelve el UUID como {@code String}, o {@code null} si no hay contexto
     * activo. Conveniente para persistir en la columna VARCHAR
     * {@code id_auditoria}.
     */
    public static String getCorrelationIdAsString() {
        UUID id = CORRELATION_ID.get();
        return id != null ? id.toString() : null;
    }

    /**
     * Limpia el contexto del hilo actual. Debe llamarse SIEMPRE en un bloque
     * {@code finally} al terminar el proceso para evitar fugas de memoria.
     */
    public static void clear() {
        CORRELATION_ID.remove();
    }
}
