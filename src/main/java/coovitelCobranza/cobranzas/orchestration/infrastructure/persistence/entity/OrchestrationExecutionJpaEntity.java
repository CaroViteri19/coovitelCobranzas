package coovitelCobranza.cobranzas.orchestration.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orchestration_executions")
public class OrchestrationExecutionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "case_id", nullable = false)
    private Long caseId;

    @Column(name = "channel", nullable = false, length = 20)
    private String channel;

    @Column(name = "destination", nullable = false, length = 120)
    private String destination;

    @Column(name = "template", nullable = false, length = 500)
    private String template;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public OrchestrationExecutionJpaEntity() {
    }

    public OrchestrationExecutionJpaEntity(Long id, Long caseId, String channel, String destination,
                                           String template, String status, LocalDateTime createdAt) {
        this.id = id;
        this.caseId = caseId;
        this.channel = channel;
        this.destination = destination;
        this.template = template;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

