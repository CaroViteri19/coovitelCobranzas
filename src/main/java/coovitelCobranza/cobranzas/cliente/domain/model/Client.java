package coovitelCobranza.cobranzas.cliente.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class Client {

    private final Long id;
    private final String tipoDocumento;
    private final String numeroDocumento;
    private String fullName;
    private String telefono;
    private String email;
    private boolean aceptaWhatsApp;
    private boolean aceptaSms;
    private boolean aceptaEmail;
    private LocalDateTime updatedAt;

    private Client(Long id,
                    String tipoDocumento,
                    String numeroDocumento,
                    String fullName,
                    String telefono,
                    String email,
                    boolean aceptaWhatsApp,
                    boolean aceptaSms,
                    boolean aceptaEmail) {
        this.id = id;
        this.tipoDocumento = Objects.requireNonNull(tipoDocumento, "tipoDocumento es requerido");
        this.numeroDocumento = Objects.requireNonNull(numeroDocumento, "numeroDocumento es requerido");
        this.fullName = Objects.requireNonNull(fullName, "fullName is required");
        this.telefono = telefono;
        this.email = email;
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = LocalDateTime.now();
    }

    public static Client create(String tipoDocumento, String numeroDocumento, String fullName) {
        return new Client(null, tipoDocumento, numeroDocumento, fullName, null, null, false, false, false);
    }

    public static Client reconstruct(Long id,
                                      String tipoDocumento,
                                      String numeroDocumento,
                                      String fullName,
                                      String telefono,
                                      String email,
                                      boolean aceptaWhatsApp,
                                      boolean aceptaSms,
                                      boolean aceptaEmail,
                                      LocalDateTime updatedAt) {
        Client client = new Client(id, tipoDocumento, numeroDocumento, fullName, telefono, email,
                aceptaWhatsApp, aceptaSms, aceptaEmail);
        client.updatedAt = updatedAt;
        return client;
    }

    public void updateContact(String telefono, String email) {
        this.telefono = telefono;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateConsents(boolean aceptaWhatsApp, boolean aceptaSms, boolean aceptaEmail) {
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

    public String getFullName() {
        return fullName;
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

