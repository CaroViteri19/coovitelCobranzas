package coovitelCobranza.cobranzas.scoring.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scoring_segmentation")
public class ScoringSegmentationJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "customer_id", nullable = false)
    private Long clienteId;

    @Column(name = "obligation_id", nullable = false)
    private Long obligacionId;

    @Column(nullable = false)
    private double score;

    @Column(name = "segment", nullable = false, length = 20)
    private String segmento;

    @Column(name = "model_version", nullable = false, length = 30)
    private String versionModelo;

    @Column(name = "main_reason", nullable = false, length = 200)
    private String razonPrincipal;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ScoringSegmentationJpaEntity() {
    }

    public ScoringSegmentationJpaEntity(Long id,
                                        Long clienteId,
                                        Long obligacionId,
                                        double score,
                                        String segmento,
                                        String versionModelo,
                                        String razonPrincipal,
                                        LocalDateTime createdAt) {
        this.id = id;
        this.clienteId = clienteId;
        this.obligacionId = obligacionId;
        this.score = score;
        this.segmento = segmento;
        this.versionModelo = versionModelo;
        this.razonPrincipal = razonPrincipal;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return clienteId;
    }

    public void setClientId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getObligationId() {
        return obligacionId;
    }

    public void setObligationId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSegment() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getModelVersion() {
        return versionModelo;
    }

    public void setVersionModelo(String versionModelo) {
        this.versionModelo = versionModelo;
    }

    public String getMainReason() {
        return razonPrincipal;
    }

    public void setRazonPrincipal(String razonPrincipal) {
        this.razonPrincipal = razonPrincipal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

