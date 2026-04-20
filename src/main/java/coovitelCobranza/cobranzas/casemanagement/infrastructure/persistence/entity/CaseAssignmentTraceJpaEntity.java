package coovitelCobranza.cobranzas.casemanagement.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "case_assignment_trace")
public class CaseAssignmentTraceJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(nullable = false, length = 100)
    private String advisor;

    @Column(name = "assignment_source", nullable = false, length = 30)
    private String assignmentSource;

    @Column(name = "performed_by", length = 80)
    private String performedBy;

    @Column(name = "performed_by_role", length = 80)
    private String performedByRole;

    @Column(name = "correlation_id", length = 100)
    private String correlationId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CaseAssignmentTraceJpaEntity() {
    }

    public CaseAssignmentTraceJpaEntity(Long id,
                                        Long caseId,
                                        String advisor,
                                        String assignmentSource,
                                        String performedBy,
                                        String performedByRole,
                                        String correlationId,
                                        LocalDateTime createdAt) {
        this.id = id;
        this.caseId = caseId;
        this.advisor = advisor;
        this.assignmentSource = assignmentSource;
        this.performedBy = performedBy;
        this.performedByRole = performedByRole;
        this.correlationId = correlationId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public String getAdvisor() {
        return advisor;
    }

    public String getAssignmentSource() {
        return assignmentSource;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public String getPerformedByRole() {
        return performedByRole;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

