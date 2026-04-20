package coovitelCobranza.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Propiedades de configuración para el bootstrap de seguridad.
 * Mapea las propiedades de la aplicación prefijadas con "app.security.bootstrap".
 */
@ConfigurationProperties(prefix = "app.security.bootstrap")
public class SecurityBootstrapProperties {

    /**
     * Indica si el bootstrap de datos iniciales está habilitado.
     */
    private boolean enabled;

    /**
     * Nombre de usuario del administrador predeterminado.
     */
    private String adminUsername;

    /**
     * Correo electrónico del administrador predeterminado.
     */
    private String adminEmail;

    /**
     * Contraseña del administrador predeterminado.
     */
    private String adminPassword;

    /**
     * Nombre completo del administrador predeterminado.
     */
    private String adminFullName;

    /**
     * Abreviatura del tipo de documento del administrador predeterminado.
     */
    private String adminDocumentType;

    /**
     * Número de documento del administrador predeterminado.
     */
    private Long adminDocument;

    /**
     * Verifica si el bootstrap está habilitado.
     *
     * @return true si está habilitado, false en caso contrario.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Establece si el bootstrap está habilitado.
     *
     * @param enabled true para habilitar, false para deshabilitar.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Obtiene el nombre de usuario del administrador.
     *
     * @return Nombre de usuario del administrador.
     */
    public String getAdminUsername() {
        return adminUsername;
    }

    /**
     * Establece el nombre de usuario del administrador.
     *
     * @param adminUsername Nombre de usuario del administrador.
     */
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }

    /**
     * Obtiene el correo electrónico del administrador.
     *
     * @return Correo electrónico del administrador.
     */
    public String getAdminEmail() {
        return adminEmail;
    }

    /**
     * Establece el correo electrónico del administrador.
     *
     * @param adminEmail Correo electrónico del administrador.
     */
    public void setAdminEmail(String adminEmail) {
        this.adminEmail = adminEmail;
    }

    /**
     * Obtiene la contraseña del administrador.
     *
     * @return Contraseña del administrador.
     */
    public String getAdminPassword() {
        return adminPassword;
    }

    /**
     * Establece la contraseña del administrador.
     *
     * @param adminPassword Contraseña del administrador.
     */
    public void setAdminPassword(String adminPassword) {
        this.adminPassword = adminPassword;
    }

    /**
     * Obtiene el nombre completo del administrador.
     *
     * @return Nombre completo del administrador.
     */
    public String getAdminFullName() {
        return adminFullName;
    }

    /**
     * Establece el nombre completo del administrador.
     *
     * @param adminFullName Nombre completo del administrador.
     */
    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }

    /**
     * Obtiene la abreviatura del tipo de documento del administrador.
     *
     * @return Abreviatura del tipo de documento.
     */
    public String getAdminDocumentType() {
        return adminDocumentType;
    }

    /**
     * Establece la abreviatura del tipo de documento del administrador.
     *
     * @param adminDocumentType Abreviatura del tipo de documento.
     */
    public void setAdminDocumentType(String adminDocumentType) {
        this.adminDocumentType = adminDocumentType;
    }

    /**
     * Obtiene el número de documento del administrador.
     *
     * @return Número de documento del administrador.
     */
    public Long getAdminDocument() {
        return adminDocument;
    }

    /**
     * Establece el número de documento del administrador.
     *
     * @param adminDocument Número de documento del administrador.
     */
    public void setAdminDocument(Long adminDocument) {
        this.adminDocument = adminDocument;
    }
}

