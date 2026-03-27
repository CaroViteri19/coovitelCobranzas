package coovitelCobranza.cobranzas.casogestion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 📋 MODELO DE CASO DE GESTIÓN (VERSIÓN EN INGLÉS)
 * 
 * Esta clase representa un CASO en el sistema de cobranzas.
 * Un caso es la gestión de una obligación específica hacia un cliente.
 * 
 * RESPONSABILIDADES:
 * - Almacenar información del caso (ID, obligación, prioridad, estado)
 * - Permitir asignar un asesor al caso
 * - Permitir programar acciones futuras
 * - Permitir cerrar el caso cuando se resuelve
 * 
 * EJEMPLO DE USO:
 *   // Crear un nuevo caso de cobranza
 *   Case miCaso = Case.create(obligationId, Priority.HIGH);
 *   
 *   // Asignar un asesor
 *   miCaso.assignAdvisor("Juan Pérez");
 *   
 *   // Programar siguiente acción
 *   miCaso.scheduleNextAction(LocalDateTime.now().plusDays(7));
 *   
 *   // Cerrar el caso cuando se paga
 *   miCaso.close();
 * 
 * ENUM Priority (Prioridad):
 *   - LOW: Baja prioridad (deuda pequeña)
 *   - MEDIUM: Media prioridad (deuda normal)
 *   - HIGH: Alta prioridad (deuda grande)
 *   - CRITICAL: Crítica (deuda muy grande o atrasada)
 * 
 * ENUM Status (Estado):
 *   - OPEN: Caso abierto, sin asignar
 *   - IN_MANAGEMENT: En gestión con un asesor
 *   - PAUSED: Pausado (en espera de respuesta)
 *   - CLOSED: Cerrado (caso resuelto)
 */
public class Case {

    private final Long id;
    private final Long obligationId;
    private Priority priority;
    private Status status;
    private String assignedAdvisor;
    private LocalDateTime nextActionAt;
    private LocalDateTime updatedAt;

    private Case(Long id,
                 Long obligationId,
                 Priority priority,
                 Status status,
                 String assignedAdvisor,
                 LocalDateTime nextActionAt) {
        this.id = id;
        this.obligationId = Objects.requireNonNull(obligationId, "obligationId is required");
        this.priority = Objects.requireNonNull(priority, "priority is required");
        this.status = Objects.requireNonNull(status, "status is required");
        this.assignedAdvisor = assignedAdvisor;
        this.nextActionAt = nextActionAt;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Factory method to create a new case.
     *
     * @param obligationId the ID of the obligation
     * @param priority     the priority level
     * @return a new Case aggregate
     */
    public static Case create(Long obligationId, Priority priority) {
        return new Case(null, obligationId, priority, Status.OPEN, null, null);
    }

    /**
     * Factory method to reconstruct a case from persistence.
     *
     * @param id              the case ID
     * @param obligationId    the obligation ID
     * @param priority        the priority level
     * @param status          the current status
     * @param assignedAdvisor the assigned advisor
     * @param nextActionAt    the next action datetime
     * @param updatedAt       the last update datetime
     * @return the reconstructed Case
     */
    public static Case reconstruct(Long id,
                                   Long obligationId,
                                   Priority priority,
                                   Status status,
                                   String assignedAdvisor,
                                   LocalDateTime nextActionAt,
                                   LocalDateTime updatedAt) {
        Case caseEntity = new Case(id, obligationId, priority, status, assignedAdvisor, nextActionAt);
        caseEntity.updatedAt = updatedAt;
        return caseEntity;
    }

    /**
     * Assign an advisor to this case and transition to IN_MANAGEMENT status.
     *
     * @param advisor the name or ID of the advisor
     * @throws IllegalArgumentException if advisor is null or blank
     */
    public void assignAdvisor(String advisor) {
        if (advisor == null || advisor.isBlank()) {
            throw new IllegalArgumentException("Advisor is required");
        }
        this.assignedAdvisor = advisor;
        this.status = Status.IN_MANAGEMENT;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Schedule the next action for this case.
     *
     * @param dateTime the datetime of the next action
     * @throws IllegalArgumentException if dateTime is null
     */
    public void scheduleNextAction(LocalDateTime dateTime) {
        this.nextActionAt = Objects.requireNonNull(dateTime, "dateTime is required");
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Close this case.
     */
    public void close() {
        this.status = Status.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public Long getId() {
        return id;
    }

    public Long getObligationId() {
        return obligationId;
    }

    public Priority getPriority() {
        return priority;
    }

    public Status getStatus() {
        return status;
    }

    public String getAssignedAdvisor() {
        return assignedAdvisor;
    }

    public LocalDateTime getNextActionAt() {
        return nextActionAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Priority levels for case management.
     */
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Case statuses throughout its lifecycle.
     */
    public enum Status {
        OPEN,
        IN_MANAGEMENT,
        PAUSED,
        CLOSED
    }
}


