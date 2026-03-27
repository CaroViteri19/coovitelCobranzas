package cooviteCobranza.cobranzas.politicas.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Politica {

    private final Long id;
    private final Long estrategiaId;
    private final TipoCobro tipoCobro;
    private int diasMoraMinimo;
    private int diasMoraMaximo;
    private String accion;
    private boolean activa;
    private LocalDateTime updatedAt;

    public enum TipoCobro {
        PROACTIVO,      // Contacto antes de mora
        REACTIVO,       // Después de vencer
        COBRANZA_LEGAL, // Escalado a abogado
        NEGOCIACION     // Planes de pago
    }

    private Politica(Long id,
                     Long estrategiaId,
                     TipoCobro tipoCobro,
                     int diasMoraMinimo,
                     int diasMoraMaximo,
                     String accion,
                     boolean activa) {
        this.id = id;
        this.estrategiaId = Objects.requireNonNull(estrategiaId, "estrategiaId es requerido");
        this.tipoCobro = Objects.requireNonNull(tipoCobro, "tipoCobro es requerido");
        this.diasMoraMinimo = diasMoraMinimo;
        this.diasMoraMaximo = diasMoraMaximo;
        this.accion = Objects.requireNonNull(accion, "accion es requerida");
        this.activa = activa;
        this.updatedAt = LocalDateTime.now();
    }

    public static Politica crear(Long estrategiaId, TipoCobro tipoCobro, String accion) {
        return new Politica(null, estrategiaId, tipoCobro, 0, 30, accion, true);
    }

    public static Politica reconstruir(Long id,
                                       Long estrategiaId,
                                       TipoCobro tipoCobro,
                                       int diasMoraMinimo,
                                       int diasMoraMaximo,
                                       String accion,
                                       boolean activa,
                                       LocalDateTime updatedAt) {
        Politica politica = new Politica(id, estrategiaId, tipoCobro, diasMoraMinimo,
                diasMoraMaximo, accion, activa);
        politica.updatedAt = updatedAt;
        return politica;
    }

    public void activar() {
        this.activa = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void desactivar() {
        this.activa = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void configurarRangoMora(int min, int max) {
        if (min < 0 || max < 0) {
            throw new IllegalArgumentException("Días no pueden ser negativos");
        }
        if (min > max) {
            throw new IllegalArgumentException("Mínimo no puede ser mayor a máximo");
        }
        this.diasMoraMinimo = min;
        this.diasMoraMaximo = max;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean aplicaA(int diasMora) {
        return diasMora >= diasMoraMinimo && diasMora <= diasMoraMaximo;
    }

    public Long getId() {
        return id;
    }

    public Long getEstrategiaId() {
        return estrategiaId;
    }

    public TipoCobro getTipoCobro() {
        return tipoCobro;
    }

    public int getDiasMoraMinimo() {
        return diasMoraMinimo;
    }

    public int getDiasMoraMaximo() {
        return diasMoraMaximo;
    }

    public String getAccion() {
        return accion;
    }

    public boolean isActiva() {
        return activa;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

