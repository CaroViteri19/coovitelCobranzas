package coovitelCobranza.cobranzas.casogestion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class CaseAssignmentTrace {

    private final Long id;
    private final Long caseId;
    private final String advisor;
    private final String assignmentSource;
    private final String performedBy;
    private final String performedByRole;
    private final String correlationId;
    private final LocalDateTime createdAt;

    private CaseAssignmentTrace(Long id,
                                Long caseId,
                                String advisor,
                                String assignmentSource,
                                String performedBy,
                                String performedByRole,
                                String correlationId,
                                LocalDateTime createdAt) {
        this.id = id;
        this.caseId = Objects.requireNonNull(caseId, "caseId is required");
        this.advisor = Objects.requireNonNull(advisor, "advisor is required");
        this.assignmentSource = Objects.requireNonNull(assignmentSource, "assignmentSource is required");
        this.performedBy = performedBy;
        this.performedByRole = performedByRole;
        this.correlationId = correlationId;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static CaseAssignmentTrace create(Long caseId,
                                             String advisor,
                                             String assignmentSource,
                                             String performedBy,
                                             String performedByRole,
                                             String correlationId) {
        return new CaseAssignmentTrace(null, caseId, advisor, assignmentSource, performedBy, performedByRole, correlationId, LocalDateTime.now());
    }

    public static CaseAssignmentTrace reconstruct(Long id,
                                                  Long caseId,
                                                  String advisor,
                                                  String assignmentSource,
                                                  String performedBy,
                                                  String performedByRole,
                                                  String correlationId,
                                                  LocalDateTime createdAt) {
        return new CaseAssignmentTrace(id, caseId, advisor, assignmentSource, performedBy, performedByRole, correlationId, createdAt);
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

