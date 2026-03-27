package cooviteCobranza.cobranzas.auditoria.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class AuditoriaEvento {

    private final Long id;
    private final String entidad;
    private final Long entidadId;
    private final String accion;
    private final String usuario;
    private final String detalle;
    private final LocalDateTime createdAt;

    private AuditoriaEvento(Long id,
                           String entidad,
                           Long entidadId,
                           String accion,
                           String usuario,
                           String detalle,
                           LocalDateTime createdAt) {
        this.id = id;
        this.entidad = Objects.requireNonNull(entidad, "entidad es requerida");
        this.entidadId = Objects.requireNonNull(entidadId, "entidadId es requerido");
        this.accion = Objects.requireNonNull(accion, "accion es requerida");
        this.usuario = Objects.requireNonNull(usuario, "usuario es requerido");
        this.detalle = detalle;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static AuditoriaEvento crear(String entidad, Long entidadId, String accion, String usuario,
                                        String detalle) {
        return new AuditoriaEvento(null, entidad, entidadId, accion, usuario, detalle, LocalDateTime.now());
    }

    public static AuditoriaEvento reconstruir(Long id,
                                              String entidad,
                                              Long entidadId,
                                              String accion,
                                              String usuario,
                                              String detalle,
                                              LocalDateTime createdAt) {
        return new AuditoriaEvento(id, entidad, entidadId, accion, usuario, detalle, createdAt);
    }

    public Long getId() {
        return id;
    }

    public String getEntidad() {
        return entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public String getAccion() {
        return accion;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getDetalle() {
        return detalle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

