package coovitelCobranza.cobranzas.scoring.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class ScoringSegmentacion {

    private final Long id;
    private final Long clienteId;
    private final Long obligacionId;
    private final double score;
    private final String segmento;
    private final String versionModelo;
    private final String razonPrincipal;
    private final LocalDateTime createdAt;

    private ScoringSegmentacion(Long id,
                                Long clienteId,
                                Long obligacionId,
                                double score,
                                String segmento,
                                String versionModelo,
                                String razonPrincipal,
                                LocalDateTime createdAt) {
        this.id = id;
        this.clienteId = Objects.requireNonNull(clienteId, "clienteId es requerido");
        this.obligacionId = Objects.requireNonNull(obligacionId, "obligacionId es requerido");
        this.score = score;
        this.segmento = Objects.requireNonNull(segmento, "segmento es requerido");
        this.versionModelo = Objects.requireNonNull(versionModelo, "versionModelo es requerido");
        this.razonPrincipal = Objects.requireNonNull(razonPrincipal, "razonPrincipal es requerida");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static ScoringSegmentacion crear(Long clienteId,
                                            Long obligacionId,
                                            double score,
                                            String segmento,
                                            String versionModelo,
                                            String razonPrincipal) {
        return new ScoringSegmentacion(null, clienteId, obligacionId, score, segmento, versionModelo,
                razonPrincipal, LocalDateTime.now());
    }

    public static ScoringSegmentacion reconstruir(Long id,
                                                  Long clienteId,
                                                  Long obligacionId,
                                                  double score,
                                                  String segmento,
                                                  String versionModelo,
                                                  String razonPrincipal,
                                                  LocalDateTime createdAt) {
        return new ScoringSegmentacion(id, clienteId, obligacionId, score, segmento, versionModelo,
                razonPrincipal, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public double getScore() {
        return score;
    }

    public String getSegmento() {
        return segmento;
    }

    public String getVersionModelo() {
        return versionModelo;
    }

    public String getRazonPrincipal() {
        return razonPrincipal;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

