package cooviteCobranza.cobranzas.interaccion.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Interaccion {

    private final Long id;
    private final Long casoGestionId;
    private final Canal canal;
    private final String plantilla;
    private EstadoResultado resultado;
    private final LocalDateTime createdAt;

    private Interaccion(Long id, Long casoGestionId, Canal canal, String plantilla, EstadoResultado resultado,
                        LocalDateTime createdAt) {
        this.id = id;
        this.casoGestionId = Objects.requireNonNull(casoGestionId, "casoGestionId es requerido");
        this.canal = Objects.requireNonNull(canal, "canal es requerido");
        this.plantilla = plantilla;
        this.resultado = Objects.requireNonNull(resultado, "resultado es requerido");
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
    }

    public static Interaccion crear(Long casoGestionId, Canal canal, String plantilla) {
        return new Interaccion(null, casoGestionId, canal, plantilla, EstadoResultado.PENDIENTE, LocalDateTime.now());
    }

    public static Interaccion reconstruir(Long id, Long casoGestionId, Canal canal, String plantilla,
                                          EstadoResultado resultado, LocalDateTime createdAt) {
        return new Interaccion(id, casoGestionId, canal, plantilla, resultado, createdAt);
    }

    public void marcarEntregada() {
        this.resultado = EstadoResultado.ENTREGADA;
    }

    public void marcarLeida() {
        this.resultado = EstadoResultado.LEIDA;
    }

    public void marcarFallida() {
        this.resultado = EstadoResultado.FALLIDA;
    }

    public Long getId() {
        return id;
    }

    public Long getCasoGestionId() {
        return casoGestionId;
    }

    public Canal getCanal() {
        return canal;
    }

    public String getPlantilla() {
        return plantilla;
    }

    public EstadoResultado getResultado() {
        return resultado;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public enum Canal {
        SMS,
        WHATSAPP,
        EMAIL,
        VOZ
    }

    public enum EstadoResultado {
        PENDIENTE,
        ENTREGADA,
        LEIDA,
        RESPONDIDA,
        FALLIDA,
        NO_CONTACTO
    }
}

