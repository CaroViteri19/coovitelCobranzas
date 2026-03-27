package coovitelCobranza.cobranzas.scoring.infrastructure.persistence;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "scoring_segmentacion")
public class ScoringSegmentacionJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long clienteId;

    @Column(nullable = false)
    private Long obligacionId;

    @Column(nullable = false)
    private double score;

    @Column(nullable = false, length = 20)
    private String segmento;

    @Column(nullable = false, length = 30)
    private String versionModelo;

    @Column(nullable = false, length = 200)
    private String razonPrincipal;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public ScoringSegmentacionJpaEntity() {
    }

    public ScoringSegmentacionJpaEntity(Long id,
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

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public void setObligacionId(Long obligacionId) {
        this.obligacionId = obligacionId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public String getSegmento() {
        return segmento;
    }

    public void setSegmento(String segmento) {
        this.segmento = segmento;
    }

    public String getVersionModelo() {
        return versionModelo;
    }

    public void setVersionModelo(String versionModelo) {
        this.versionModelo = versionModelo;
    }

    public String getRazonPrincipal() {
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

