package coovitelCobranza.cobranzas.orquestacion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class OrquestacionEjecucion {

    private final Long id;
    private final Long casoGestionId;
    private final String canal;
    private final String destino;
    private final String plantilla;
    private Estado estado;
    private final LocalDateTime createdAt;

    public enum Estado {
        PENDIENTE,
        ENVIADO,
        FALLIDO
    }

    private OrquestacionEjecucion(Long id,
                                  Long casoGestionId,
                                  String canal,
                                  String destino,
                                  String plantilla,
                                  Estado estado,
                                  LocalDateTime createdAt) {
        this.id = id;
        this.casoGestionId = Objects.requireNonNull(casoGestionId, "casoGestionId es requerido");
        this.canal = Objects.requireNonNull(canal, "canal es requerido");
        this.destino = Objects.requireNonNull(destino, "destino es requerido");
        this.plantilla = Objects.requireNonNull(plantilla, "plantilla es requerida");
        this.estado = Objects.requireNonNull(estado, "estado es requerido");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static OrquestacionEjecucion crear(Long casoGestionId, String canal, String destino, String plantilla) {
        return new OrquestacionEjecucion(null, casoGestionId, canal, destino, plantilla, Estado.PENDIENTE,
                LocalDateTime.now());
    }

    public static OrquestacionEjecucion reconstruir(Long id,
                                                    Long casoGestionId,
                                                    String canal,
                                                    String destino,
                                                    String plantilla,
                                                    Estado estado,
                                                    LocalDateTime createdAt) {
        return new OrquestacionEjecucion(id, casoGestionId, canal, destino, plantilla, estado, createdAt);
    }

    public void marcarEnviado() {
        this.estado = Estado.ENVIADO;
    }

    public void marcarFallido() {
        this.estado = Estado.FALLIDO;
    }

    public Long getId() {
        return id;
    }

    public Long getCasoGestionId() {
        return casoGestionId;
    }

    public String getCanal() {
        return canal;
    }

    public String getDestino() {
        return destino;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public Estado getEstado() {
        return estado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

