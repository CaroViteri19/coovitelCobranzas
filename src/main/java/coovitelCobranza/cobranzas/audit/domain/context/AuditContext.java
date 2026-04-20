package coovitelCobranza.cobranzas.audit.domain.context;

import java.util.UUID;

/**
 * Audit context based on {@link ThreadLocal} that propagates a correlation
 * identifier throughout the lifecycle of the current thread.
 *
 * <p>The ID is generated at the start of a logical process and is consulted
 * automatically by {@code AuditService} when registering any event. In this
 * way, all events emitted within the same thread share the same UUID and can
 * be filtered by that ID in the {@code correlation_id} column of the
 * {@code audit_events} table.
 *
 * <p><b>Recommended lifecycle:</b>
 * <pre>{@code
 * AuditContext.startNewContext();
 * try {
 *     // ... business logic that registers audit events ...
 * } finally {
 *     AuditContext.clear();
 * }
 * }</pre>
 *
 * <p><b>IMPORTANT:</b> {@link #clear()} must always be invoked in a
 * {@code finally} block. Omitting it causes memory leaks in thread-pool based
 * servers (Tomcat reuses threads between requests).
 */
public final class AuditContext {

    private static final ThreadLocal<UUID> CORRELATION_ID = new ThreadLocal<>();

    private AuditContext() {
        // Utility class: not instantiable.
    }

    /**
     * Generates a new random UUID and stores it in the current thread as the
     * correlation ID. Overwrites any previous value.
     *
     * @return the newly generated UUID
     */
    public static UUID startNewContext() {
        UUID id = UUID.randomUUID();
        CORRELATION_ID.set(id);
        return id;
    }

    /**
     * Manually sets the current thread correlation ID. Useful for propagating
     * an ID received from another layer (for example, an HTTP header).
     */
    public static void setCorrelationId(UUID id) {
        CORRELATION_ID.set(id);
    }

    /**
     * Returns the UUID associated with the current thread, or {@code null} if
     * there is no active context.
     */
    public static UUID getCorrelationId() {
        return CORRELATION_ID.get();
    }

    /**
     * Returns the UUID as a {@code String}, or {@code null} if there is no
     * active context. Useful for persisting in the {@code correlation_id}
     * VARCHAR column.
     */
    public static String getCorrelationIdAsString() {
        UUID id = CORRELATION_ID.get();
        return id != null ? id.toString() : null;
    }

    /**
     * Clears the current thread context. It must ALWAYS be called in a
     * {@code finally} block at the end of the process to avoid memory leaks.
     */
    public static void clear() {
        CORRELATION_ID.remove();
    }
}
