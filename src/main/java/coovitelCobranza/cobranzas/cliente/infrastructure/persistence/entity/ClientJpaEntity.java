package coovitelCobranza.cobranzas.cliente.infrastructure.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un cliente en la base de datos.
 * Mapea la tabla "clientes" con los atributos del modelo de dominio.
 */
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

    @Column(name = "telefono_2", length = 15)
    private String telefono2;

    @Column(length = 100)
    private String email;

    @Column(name = "ciudad", length = 60)
    private String ciudad;

    @Column(name = "canal_preferido", length = 20)
    private String canalPreferido;

    @Column(nullable = false)
    private boolean aceptaWhatsApp;

    @Column(nullable = false)
    private boolean aceptaSms;

    @Column(nullable = false)
    private boolean aceptaEmail;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor sin argumentos requerido por JPA.
     */
    public ClientJpaEntity() {
    }

    /**
     * Construye una entidad de cliente con todos los atributos.
     *
     * @param id identificador único del cliente
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @param fullName nombre completo del cliente
     * @param telefono número de teléfono
     * @param email dirección de correo electrónico
     * @param aceptaWhatsApp consentimiento para WhatsApp
     * @param aceptaSms consentimiento para SMS
     * @param aceptaEmail consentimiento para correo electrónico
     * @param updatedAt fecha y hora de última actualización
     */
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

    /**
     * Obtiene el identificador único del cliente.
     *
     * @return id del cliente
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador único del cliente.
     *
     * @param id identificador a asignar
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el tipo de documento de identidad del cliente.
     *
     * @return tipo de documento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
    }

    /**
     * Establece el tipo de documento de identidad del cliente.
     *
     * @param tipoDocumento tipo de documento
     */
    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    /**
     * Obtiene el número de documento de identidad del cliente.
     *
     * @return número de documento
     */
    public String getNumeroDocumento() {
        return numeroDocumento;
    }

    /**
     * Establece el número de documento de identidad del cliente.
     *
     * @param numeroDocumento número de documento
     */
    public void setNumeroDocumento(String numeroDocumento) {
        this.numeroDocumento = numeroDocumento;
    }

    /**
     * Obtiene el nombre completo del cliente.
     *
     * @return nombre completo
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Establece el nombre completo del cliente.
     *
     * @param fullName nombre completo
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Obtiene el número de teléfono del cliente.
     *
     * @return teléfono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Establece el número de teléfono del cliente.
     *
     * @param telefono número de teléfono
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public void setTelefono2(String telefono2) {
        this.telefono2 = telefono2;
    }

    /**
     * Obtiene el correo electrónico del cliente.
     *
     * @return correo electrónico
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del cliente.
     *
     * @param email correo electrónico
     */
    public void setEmail(String email) {
        this.email = email;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getCanalPreferido() {
        return canalPreferido;
    }

    public void setCanalPreferido(String canalPreferido) {
        this.canalPreferido = canalPreferido;
    }

    /**
     * Verifica si el cliente acepta comunicación por WhatsApp.
     *
     * @return true si acepta, false en caso contrario
     */
    public boolean isAceptaWhatsApp() {
        return aceptaWhatsApp;
    }

    /**
     * Establece el consentimiento para comunicación por WhatsApp.
     *
     * @param aceptaWhatsApp true para aceptar, false para rechazar
     */
    public void setAceptaWhatsApp(boolean aceptaWhatsApp) {
        this.aceptaWhatsApp = aceptaWhatsApp;
    }

    /**
     * Verifica si el cliente acepta comunicación por SMS.
     *
     * @return true si acepta, false en caso contrario
     */
    public boolean isAceptaSms() {
        return aceptaSms;
    }

    /**
     * Establece el consentimiento para comunicación por SMS.
     *
     * @param aceptaSms true para aceptar, false para rechazar
     */
    public void setAceptaSms(boolean aceptaSms) {
        this.aceptaSms = aceptaSms;
    }

    /**
     * Verifica si el cliente acepta comunicación por correo electrónico.
     *
     * @return true si acepta, false en caso contrario
     */
    public boolean isAceptaEmail() {
        return aceptaEmail;
    }

    /**
     * Establece el consentimiento para comunicación por correo electrónico.
     *
     * @param aceptaEmail true para aceptar, false para rechazar
     */
    public void setAceptaEmail(boolean aceptaEmail) {
        this.aceptaEmail = aceptaEmail;
    }

    /**
     * Obtiene la fecha y hora de última actualización del cliente.
     *
     * @return fecha y hora de actualización
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Establece la fecha y hora de última actualización del cliente.
     *
     * @param updatedAt fecha y hora de actualización
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

