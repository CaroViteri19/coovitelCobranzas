package coovitelCobranza.cobranzas.politicas.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Estrategia {

    private final Long id;
    private final String nombre;
    private final String descripcion;
    private boolean activa;
    private int maxIntentosContacto;
    private int diasAntesDeeEscalacion;
    private String rolAsignacionEscalada;
    private LocalDateTime updatedAt;

    private Estrategia(Long id,
                       String nombre,
                       String descripcion,
                       boolean activa,
                       int maxIntentosContacto,
                       int diasAntesDeeEscalacion,
                       String rolAsignacionEscalada) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "nombre es requerido");
        this.descripcion = descripcion;
        this.activa = activa;
        this.maxIntentosContacto = maxIntentosContacto;
        this.diasAntesDeeEscalacion = diasAntesDeeEscalacion;
        this.rolAsignacionEscalada = rolAsignacionEscalada;
        this.updatedAt = LocalDateTime.now();
    }

    public static Estrategia crear(String nombre, String descripcion) {
        return new Estrategia(null, nombre, descripcion, true, 3, 5, "SUPERVISOR");
    }

    public static Estrategia reconstruir(Long id,
                                        String nombre,
                                        String descripcion,
                                        boolean activa,
                                        int maxIntentosContacto,
                                        int diasAntesDeeEscalacion,
                                        String rolAsignacionEscalada,
                                        LocalDateTime updatedAt) {
        Estrategia estrategia = new Estrategia(id, nombre, descripcion, activa,
                maxIntentosContacto, diasAntesDeeEscalacion, rolAsignacionEscalada);
        estrategia.updatedAt = updatedAt;
        return estrategia;
    }

    public void activar() {
        this.activa = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void desactivar() {
        this.activa = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void configurarParametros(int maxIntentos, int diasEscalacion, String rolEscalacion) {
        if (maxIntentos <= 0) {
            throw new IllegalArgumentException("maxIntentos debe ser mayor a 0");
        }
        if (diasEscalacion < 0) {
            throw new IllegalArgumentException("diasEscalacion no puede ser negativo");
        }
        this.maxIntentosContacto = maxIntentos;
        this.diasAntesDeeEscalacion = diasEscalacion;
        this.rolAsignacionEscalada = Objects.requireNonNull(rolEscalacion, "rolEscalacion es requerido");
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public boolean isActiva() {
        return activa;
    }

    public int getMaxIntentosContacto() {
        return maxIntentosContacto;
    }

    public int getDiasAntesDeeEscalacion() {
        return diasAntesDeeEscalacion;
    }

    public String getRolAsignacionEscalada() {
        return rolAsignacionEscalada;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

