package coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un caso de cobranza en la base de datos.
 *
 * Mapea la tabla 'casos_gestion' y proporciona la persistencia de casos
 * de cobranza usando JPA/Hibernate.
 */
@Entity
@Table(name = "case_management")
public class CaseJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "obligation_id", nullable = false)
    private Long obligacionId;

    @Column(name = "priority", nullable = false, length = 20)
    private String prioridad;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(name = "assigned_advisor", length = 100)
    private String asesorAsignado;

    @Column(name = "next_action_at")
    private LocalDateTime proximaActionAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor sin argumentos requerido por JPA.
     */
    public CaseJpaEntity() {
    }

    /**
     * Construye una entidad de caso con todos los datos.
     *
     * @param id el identificador del caso
     * @param obligacionId el ID de la obligación asociada
     * @param prioridad el nivel de prioridad
     * @param estado el estado actual del caso
     * @param asesorAsignado el nombre del asesor asignado
     * @param proximaActionAt la fecha de la próxima acción
     * @param updatedAt la fecha de la última actualización
     */
    public CaseJpaEntity(Long id, Long obligacionId, String prioridad, String estado,
                               String asesorAsignado, LocalDateTime proximaActionAt, LocalDateTime updatedAt) {
        this.id = id;
        this.obligacionId = obligacionId;
        this.prioridad = prioridad;
        this.status = estado;
        this.asesorAsignado = asesorAsignado;
        this.proximaActionAt = proximaActionAt;
        this.updatedAt = updatedAt;
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
     * Establece el identificador único del caso.
     *
     * @param id el ID a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el ID de la obligación.
     *
     * @return el ID de la obligación
     */
    public Long getObligationId() {
        return obligacionId;
    }

    /**
     * Establece el ID de la obligación.
     *
     * @param obligacionId el ID a asignar
     */
    public void setObligationId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    /**
     * Retorna el nivel de prioridad.
     *
     * @return la prioridad como string
     */
    public String getPriority() {
        return prioridad;
    }

    /**
     * Establece el nivel de prioridad.
     *
     * @param prioridad la prioridad a asignar
     */
    public void setPriority(String prioridad) {
        this.prioridad = prioridad;
    }

    /**
     * Retorna el estado del caso.
     *
     * @return el estado como string
     */
    public String getStatus() {
        return status;
    }

    /**
     * Establece el estado del caso.
     *
     * @param estado el estado a asignar
     */
    public void setStatus(String estado) {
        this.status = estado;
    }

    /**
     * Retorna el nombre del asesor asignado.
     *
     * @return el nombre del asesor
     */
    public String getAssignedAdvisor() {
        return asesorAsignado;
    }

    /**
     * Establece el nombre del asesor asignado.
     *
     * @param asesorAsignado el nombre del asesor
     */
    public void setAdvisorAsignado(String asesorAsignado) {
        this.asesorAsignado = asesorAsignado;
    }

    /**
     * Retorna la fecha de la próxima acción.
     *
     * @return la próxima acción como LocalDateTime
     */
    public LocalDateTime getNextActionAt() {
        return proximaActionAt;
    }

    /**
     * Establece la fecha de la próxima acción.
     *
     * @param proximaActionAt la fecha a asignar
     */
    public void setProximaActionAt(LocalDateTime proximaActionAt) {
        this.proximaActionAt = proximaActionAt;
    }

    /**
     * Retorna la fecha de la última actualización.
     *
     * @return la última actualización como LocalDateTime
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Establece la fecha de la última actualización.
     *
     * @param updatedAt la fecha a asignar
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

