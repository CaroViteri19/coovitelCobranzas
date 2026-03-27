package cooviteCobranza.cobranzas.cliente.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Cliente {

    private final Long id;
    private final String tipoDocumento;
    private final String numeroDocumento;
    private String nombreCompleto;
    private String telefono;
    private String email;
    private boolean aceptaWhatsApp;
    private boolean aceptaSms;
    private boolean aceptaEmail;
    private LocalDateTime updatedAt;

    private Cliente(Long id,
                    String tipoDocumento,
                    String numeroDocumento,
                    String nombreCompleto,
                    String telefono,
                    String email,
                    boolean aceptaWhatsApp,
                    boolean aceptaSms,
                    boolean aceptaEmail) {
        this.id = id;
        this.tipoDocumento = Objects.requireNonNull(tipoDocumento, "tipoDocumento es requerido");
        this.numeroDocumento = Objects.requireNonNull(numeroDocumento, "numeroDocumento es requerido");
        this.nombreCompleto = Objects.requireNonNull(nombreCompleto, "nombreCompleto es requerido");
        this.telefono = telefono;
        this.email = email;
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public static Cliente crear(String tipoDocumento, String numeroDocumento, String nombreCompleto) {
        return new Cliente(null, tipoDocumento, numeroDocumento, nombreCompleto, null, null, false, false, false);
    }

    public static Cliente reconstruir(Long id,
                                      String tipoDocumento,
                                      String numeroDocumento,
                                      String nombreCompleto,
                                      String telefono,
                                      String email,
                                      boolean aceptaWhatsApp,
                                      boolean aceptaSms,
                                      boolean aceptaEmail,
                                      LocalDateTime updatedAt) {
        Cliente cliente = new Cliente(id, tipoDocumento, numeroDocumento, nombreCompleto, telefono, email,
                aceptaWhatsApp, aceptaSms, aceptaEmail);
        cliente.updatedAt = updatedAt;
        return cliente;
    }

    public void actualizarContacto(String telefono, String email) {
        this.telefono = telefono;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void actualizarConsentimientos(boolean aceptaWhatsApp, boolean aceptaSms, boolean aceptaEmail) {
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getEmail() {
        return email;
    }

    public boolean isAceptaWhatsApp() {
        return aceptaWhatsApp;
    }

    public boolean isAceptaSms() {
        return aceptaSms;
    }

    public boolean isAceptaEmail() {
        return aceptaEmail;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

