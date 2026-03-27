package coovitelCobranza.cobranzas.casogestion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class CasoGestion {

    private final Long id;
    private final Long obligacionId;
    private Prioridad prioridad;
    private Estado estado;
    private String asesorAsignado;
    private LocalDateTime proximaAccionAt;
    private LocalDateTime updatedAt;

    private CasoGestion(Long id,
                        Long obligacionId,
                        Prioridad prioridad,
                        Estado estado,
                        String asesorAsignado,
                        LocalDateTime proximaAccionAt) {
        this.id = id;
        this.obligacionId = Objects.requireNonNull(obligacionId, "obligacionId es requerido");
        this.prioridad = Objects.requireNonNull(prioridad, "prioridad es requerida");
        this.estado = Objects.requireNonNull(estado, "estado es requerido");
        this.asesorAsignado = asesorAsignado;
        this.proximaAccionAt = proximaAccionAt;
        this.updatedAt = LocalDateTime.now();
    }

    public static CasoGestion crear(Long obligacionId, Prioridad prioridad) {
        return new CasoGestion(null, obligacionId, prioridad, Estado.ABIERTO, null, null);
    }

    public static CasoGestion reconstruir(Long id,
                                          Long obligacionId,
                                          Prioridad prioridad,
                                          Estado estado,
                                          String asesorAsignado,
                                          LocalDateTime proximaAccionAt,
                                          LocalDateTime updatedAt) {
        CasoGestion caso = new CasoGestion(id, obligacionId, prioridad, estado, asesorAsignado, proximaAccionAt);
        caso.updatedAt = updatedAt;
        return caso;
    }

    public void asignarAsesor(String asesor) {
        if (asesor == null || asesor.isBlank()) {
            throw new IllegalArgumentException("asesor es requerido");
        }
        this.asesorAsignado = asesor;
        this.estado = Estado.EN_GESTION;
        this.updatedAt = LocalDateTime.now();
    }

    public void programarSiguienteAccion(LocalDateTime fechaHora) {
        this.proximaAccionAt = Objects.requireNonNull(fechaHora, "fechaHora es requerida");
        this.updatedAt = LocalDateTime.now();
    }

    public void cerrar() {
        this.estado = Estado.CERRADO;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getObligacionId() {
        return obligacionId;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public Estado getEstado() {
        return estado;
    }

    public String getAsesorAsignado() {
        return asesorAsignado;
    }

    public LocalDateTime getProximaAccionAt() {
        return proximaAccionAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public enum Prioridad {
        BAJA,
        MEDIA,
        ALTA,
        CRITICA
    }

    public enum Estado {
        ABIERTO,
        EN_GESTION,
        PAUSADO,
        CERRADO
    }
}

