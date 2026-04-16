package coovitelCobranza.cobranzas.cliente.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Modelo de dominio que representa un cliente en el sistema.
 * Encapsula la lógica de negocio relacionada con datos de clientes,
 * incluyendo información de contacto y consentimientos de comunicación.
 */
public class Client {

    private final Long id;
    private final String tipoDocumento;
    private final String numeroDocumento;
    private String fullName;
    private String telefono;
    private String email;
    // Campos extendidos para ingesta batch
    private String telefono2;
    private String ciudad;
    private String canalPreferido;
    private boolean aceptaWhatsApp;
    private boolean aceptaSms;
    private boolean aceptaEmail;
    private LocalDateTime updatedAt;

    /**
     * Constructor privado para la creación controlada de instancias.
     *
     * @param id identificador único del cliente
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @param fullName nombre completo del cliente
     * @param telefono número de teléfono del cliente
     * @param email dirección de correo electrónico del cliente
     * @param aceptaWhatsApp consentimiento para comunicación por WhatsApp
     * @param aceptaSms consentimiento para comunicación por SMS
     * @param aceptaEmail consentimiento para comunicación por correo electrónico
     */
    private Client(Long id,
                    String tipoDocumento,
                    String numeroDocumento,
                    String fullName,
                    String telefono,
                    String email,
                    String telefono2,
                    String ciudad,
                    String canalPreferido,
                    boolean aceptaWhatsApp,
                    boolean aceptaSms,
                    boolean aceptaEmail) {
        this.id = id;
        this.tipoDocumento = Objects.requireNonNull(tipoDocumento, "tipoDocumento es requerido");
        this.numeroDocumento = Objects.requireNonNull(numeroDocumento, "numeroDocumento es requerido");
        this.fullName = Objects.requireNonNull(fullName, "fullName is required");
        this.telefono = telefono;
        this.email = email;
        this.telefono2 = telefono2;
        this.ciudad = ciudad;
        this.canalPreferido = canalPreferido;
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Factory method para crear un nuevo cliente.
     *
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @param fullName nombre completo del cliente
     * @return nueva instancia de Cliente
     */
    public static Client create(String tipoDocumento, String numeroDocumento, String fullName) {
        return new Client(null, tipoDocumento, numeroDocumento, fullName,
                null, null, null, null, null, false, false, false);
    }

    /**
     * Factory method para reconstruir un cliente desde datos persistidos.
     *
     * @param id identificador único del cliente
     * @param tipoDocumento tipo de documento de identidad
     * @param numeroDocumento número de documento de identidad
     * @param fullName nombre completo del cliente
     * @param telefono número de teléfono del cliente
     * @param email dirección de correo electrónico del cliente
     * @param aceptaWhatsApp consentimiento para comunicación por WhatsApp
     * @param aceptaSms consentimiento para comunicación por SMS
     * @param aceptaEmail consentimiento para comunicación por correo electrónico
     * @param updatedAt fecha y hora de última actualización
     * @return instancia de Cliente reconstruida
     */
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
                null, null, null, aceptaWhatsApp, aceptaSms, aceptaEmail);
        client.updatedAt = updatedAt;
        return client;
    }

    public static Client reconstructFull(Long id,
                                          String tipoDocumento,
                                          String numeroDocumento,
                                          String fullName,
                                          String telefono,
                                          String email,
                                          String telefono2,
                                          String ciudad,
                                          String canalPreferido,
                                          boolean aceptaWhatsApp,
                                          boolean aceptaSms,
                                          boolean aceptaEmail,
                                          LocalDateTime updatedAt) {
        Client client = new Client(id, tipoDocumento, numeroDocumento, fullName, telefono, email,
                telefono2, ciudad, canalPreferido, aceptaWhatsApp, aceptaSms, aceptaEmail);
        client.updatedAt = updatedAt;
        return client;
    }

    /**
     * Actualiza la información de contacto del cliente.
     *
     * @param telefono nuevo número de teléfono
     * @param email nuevo correo electrónico
     */
    public void updateContact(String telefono, String email) {
        this.telefono = telefono;
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza todos los campos modificables provenientes de una carga batch.
     * Aplica las reglas de negocio del proceso de importación:
     * nombre, teléfonos, email, ciudad y canal preferido.
     * Los campos opcionales solo se sobreescriben si vienen con valor.
     *
     * @param nombre         nombre completo actualizado
     * @param telefono1      teléfono principal
     * @param email          correo electrónico (nullable)
     * @param telefono2      teléfono secundario (nullable)
     * @param ciudad         ciudad (nullable)
     * @param canalPreferido canal de contacto preferido (nullable)
     */
    public void updateFromBatch(String nombre,
                                 String telefono1,
                                 String email,
                                 String telefono2,
                                 String ciudad,
                                 String canalPreferido) {
        this.fullName = nombre;
        this.telefono = telefono1;
        this.email    = email;
        if (telefono2    != null) this.telefono2    = telefono2;
        if (ciudad       != null) this.ciudad       = ciudad;
        if (canalPreferido != null) this.canalPreferido = canalPreferido;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Actualiza los consentimientos de comunicación del cliente.
     *
     * @param aceptaWhatsApp nuevo consentimiento para WhatsApp
     * @param aceptaSms nuevo consentimiento para SMS
     * @param aceptaEmail nuevo consentimiento para correo electrónico
     */
    public void updateConsents(boolean aceptaWhatsApp, boolean aceptaSms, boolean aceptaEmail) {
        this.aceptaWhatsApp = aceptaWhatsApp;
        this.aceptaSms = aceptaSms;
        this.aceptaEmail = aceptaEmail;
        this.updatedAt = LocalDateTime.now();
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
     * Obtiene el tipo de documento de identidad del cliente.
     *
     * @return tipo de documento
     */
    public String getTipoDocumento() {
        return tipoDocumento;
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
     * Obtiene el nombre completo del cliente.
     *
     * @return nombre completo
     */
    public String getFullName() {
        return fullName;
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
     * Obtiene el correo electrónico del cliente.
     *
     * @return correo electrónico
     */
    public String getEmail() {
        return email;
    }

    public String getTelefono2() {
        return telefono2;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getCanalPreferido() {
        return canalPreferido;
    }

    /**
     * Verifica si el cliente acepta comunicación por WhatsApp.
     *
     * @return true si acepta WhatsApp, false en caso contrario
     */
    public boolean isAceptaWhatsApp() {
        return aceptaWhatsApp;
    }

    /**
     * Verifica si el cliente acepta comunicación por SMS.
     *
     * @return true si acepta SMS, false en caso contrario
     */
    public boolean isAceptaSms() {
        return aceptaSms;
    }

    /**
     * Verifica si el cliente acepta comunicación por correo electrónico.
     *
     * @return true si acepta correo electrónico, false en caso contrario
     */
    public boolean isAceptaEmail() {
        return aceptaEmail;
    }

    /**
     * Obtiene la fecha y hora de la última actualización del cliente.
     *
     * @return fecha y hora de actualización
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}

