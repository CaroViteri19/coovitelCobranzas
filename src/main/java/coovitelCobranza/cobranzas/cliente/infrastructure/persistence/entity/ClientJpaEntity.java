package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "clientes")
public class ClientJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String tipoDocumento;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroDocumento;

    @Column(name = "name_completo", nullable = false, length = 100)
    private String fullName;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(nullable = false)
    private boolean aceptaWhatsApp;

    @Column(nullable = false)
    private boolean aceptaSms;

    @Column(nullable = false)
    private boolean aceptaEmail;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Constructores
    public ClientJpaEntity() {
    }

    public ClientJpaEntity(Long id, String tipoDocumento, String numeroDocumento, String fullName,
                           String telefono, String email, boolean aceptaWhatsApp, boolean aceptaSms,
                           boolean aceptaEmail, LocalDateTime updatedAt) {
        this.id = id;
        this.tipoDocumento = tipoDocumento;
        this.numeroDocumento = numeroDocumento;
        this.fullName = fullName;
        this.telefono = telefono;
        this.email = email;
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = updatedAt;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAceptaWhatsApp() {
        return aceptaWhatsApp;
    }

    public void setAceptaWhatsApp(boolean aceptaWhatsApp) {
        this.aceptaWhatsApp = aceptaWhatsApp;
    }

    public boolean isAceptaSms() {
        return aceptaSms;
    }

    public void setAceptaSms(boolean aceptaSms) {
        this.aceptaSms = aceptaSms;
    }

    public boolean isAceptaEmail() {
        return aceptaEmail;
    }

    public void setAceptaEmail(boolean aceptaEmail) {
        this.aceptaEmail = aceptaEmail;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

