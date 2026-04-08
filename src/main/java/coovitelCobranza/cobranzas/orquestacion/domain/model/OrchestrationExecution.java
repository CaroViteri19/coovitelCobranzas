package coovitelCobranza.cobranzas.orquestacion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrchestrationExecution {

    private final Long id;
    private final Long casoGestionId;
    private final String canal;
    private final String destino;
    private final String plantilla;
    private Status estado;
    private final LocalDateTime createdAt;

    public enum Status {
        PENDIENTE,
        ENVIADO,
        FALLIDO
    }

    private OrchestrationExecution(Long id,
                                  Long casoGestionId,
                                  String canal,
                                  String destino,
                                  String plantilla,
                                  Status estado,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.casoGestionId = Objects.requireNonNull(casoGestionId, "casoGestionId es requerido");
        this.canal = Objects.requireNonNull(canal, "canal es requerido");
        this.destino = Objects.requireNonNull(destino, "destino es requerido");
        this.plantilla = Objects.requireNonNull(plantilla, "plantilla es requerida");
        this.estado = Objects.requireNonNull(estado, "estado es requerido");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static OrchestrationExecution crear(Long casoGestionId, String canal, String destino, String plantilla) {
        return new OrchestrationExecution(null, casoGestionId, canal, destino, plantilla, Status.PENDIENTE,
                LocalDateTime.now());
    }

    public static OrchestrationExecution reconstruct(Long id,
                                                    Long casoGestionId,
                                                    String canal,
                                                    String destino,
                                                    String plantilla,
                                                    Status estado,
                                                    LocalDateTime createdAt) {
        return new OrchestrationExecution(id, casoGestionId, canal, destino, plantilla, estado, createdAt);
    }

    public void marcarEnviado() {
        this.estado = Status.ENVIADO;
    }

    public void markSent() {
        marcarEnviado();
    }

    public void marcarFallido() {
        this.estado = Status.FALLIDO;
    }

    public void markFailed() {
        marcarFallido();
    }

    public Long getId() {
        return id;
    }

    public Long getCaseId() {
        return casoGestionId;
    }

    public String getChannel() {
        return canal;
    }

    public String getDestino() {
        return destino;
    }

    public String getTemplate() {
        return plantilla;
    }

    public Status getStatus() {
        return estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
