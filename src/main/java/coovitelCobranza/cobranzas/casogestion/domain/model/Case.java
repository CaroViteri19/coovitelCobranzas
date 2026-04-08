package coovitelCobranza.cobranzas.casogestion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio que representa un caso de cobranza.
 *
 * Un caso es la gestión de una obligación específica hacia un cliente.
 * Proporciona la lógica de negocio para asignar asesores, programar
 * acciones futuras y controlar el ciclo de vida del caso.
 *
 * Responsabilidades:
 * - Almacenar información del caso (ID, obligación, prioridad, estado)
 * - Validar y asignar un asesor al caso
 * - Programar la próxima acción sobre el caso
 * - Cerrar el caso cuando se resuelve
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

    public static Case crear(Long obligationId, Priority priority) {
        return create(obligationId, priority);
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
     * Alias en español para assignAdvisor().
     *
     * @param advisor el nombre o ID del asesor
     */
    public void asignarAdvisor(String advisor) {
        assignAdvisor(advisor);
    }

    /**
     * Programa la próxima acción para este caso.
     *
     * @param dateTime la fecha y hora de la próxima acción
     * @throws IllegalArgumentException si dateTime es null
     */
    public void scheduleNextAction(LocalDateTime dateTime) {
        this.nextActionAt = Objects.requireNonNull(dateTime, "dateTime is required");
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Alias en español para scheduleNextAction().
     *
     * @param dateTime la fecha y hora de la próxima acción
     */
    public void programarSiguienteAction(LocalDateTime dateTime) {
        scheduleNextAction(dateTime);
    }

    /**
     * Cierra este caso marcándolo como resuelto.
     */
    public void close() {
        this.status = Status.CLOSED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Alias en español para close().
     */
    public void cerrar() {
        close();
    }

    /**
     * Retorna el identificador único del caso.
     *
     * @return el ID del caso
     */
    public Long getId() {
        return id;
    }

    /**
     * Retorna el ID de la obligación asociada a este caso.
     *
     * @return el ID de la obligación
     */
    public Long getObligationId() {
        return obligationId;
    }

    /**
     * Retorna el nivel de prioridad del caso.
     *
     * @return la prioridad (LOW, MEDIUM, HIGH, CRITICAL)
     */
    public Priority getPriority() {
        return priority;
    }

    /**
     * Alias en español para getPriority().
     *
     * @return la prioridad
     */
    public Priority getPrioridad() {
        return getPriority();
    }

    /**
     * Retorna el estado actual del caso.
     *
     * @return el estado (OPEN, IN_MANAGEMENT, PAUSED, CLOSED)
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Alias en español para getStatus().
     *
     * @return el estado
     */
    public Status getEstado() {
        return getStatus();
    }

    /**
     * Retorna el nombre del asesor asignado al caso.
     *
     * @return el nombre del asesor, o null si no está asignado
     */
    public String getAssignedAdvisor() {
        return assignedAdvisor;
    }

    /**
     * Alias en español para getAssignedAdvisor().
     *
     * @return el nombre del asesor
     */
    public String getAdvisorAsignado() {
        return getAssignedAdvisor();
    }

    /**
     * Retorna la fecha y hora de la próxima acción programada.
     *
     * @return la próxima acción, o null si no está programada
     */
    public LocalDateTime getNextActionAt() {
        return nextActionAt;
    }

    /**
     * Alias en español para getNextActionAt().
     *
     * @return la próxima acción
     */
    public LocalDateTime getProximaActionAt() {
        return getNextActionAt();
    }

    /**
     * Retorna la fecha y hora de la última actualización del caso.
     *
     * @return la última actualización
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Enumeración de niveles de prioridad para la gestión de casos.
     *
     * LOW: Prioridad baja (deuda pequeña)
     * MEDIUM: Prioridad media (deuda normal)
     * HIGH: Prioridad alta (deuda grande)
     * CRITICAL: Prioridad crítica (deuda muy grande o severamente atrasada)
     */
    public enum Priority {
        LOW,
        MEDIUM,
        HIGH,
        CRITICAL
    }

    /**
     * Enumeración de estados posibles a lo largo del ciclo de vida de un caso.
     *
     * OPEN: Caso abierto, sin asignar a ningún asesor
     * IN_MANAGEMENT: Caso en gestión con un asesor asignado
     * PAUSED: Caso pausado, en espera de respuesta o evento
     * CLOSED: Caso cerrado y resuelto
     */
    public enum Status {
        OPEN,
        IN_MANAGEMENT,
        PAUSED,
        CLOSED
    }
}
