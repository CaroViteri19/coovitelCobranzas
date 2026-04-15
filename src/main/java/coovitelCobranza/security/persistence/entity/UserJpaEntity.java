package coovitelCobranza.security.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un usuario en el sistema.
 * Almacena información de autenticación, perfil y roles del usuario.
 */
@Entity
@Table(name = "users")
public class UserJpaEntity {

    /**
     * Identificador único del usuario.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    /**
     * Nombre de usuario (único, máximo 80 caracteres).
     */
    @Column(nullable = false, unique = true, length = 80)
    private String username;

    /**
     * Contraseña del usuario codificada (máximo 255 caracteres).
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Nombre completo del usuario (máximo 150 caracteres).
     */
    @Column(name = "full_name", nullable = false, length = 150)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "type_document_id", nullable = false)
    private TypeDocumentEntity typeDocument;

    @Column(name = "document", nullable = false, length = 10)
    private Long document;

    /**
     * Primer nombre del usuario (máximo 255 caracteres).
     */
    @Column(name = "name", nullable = false, length = 255)
    private String firstName;

    /**
     * Apellido del usuario (máximo 255 caracteres).
     */
    @Column(name = "lastname", nullable = false, length = 255)
    private String lastName;

    /**
     * Correo electrónico del usuario (único, máximo 120 caracteres).
     */
    @Column(length = 120, unique = true)
    private String email;

    /**
     * Indica si la cuenta del usuario está habilitada.
     */
    @Column(nullable = false)
    private boolean enabled;

    @Column(nullable = false, columnDefinition = "boolean DEFAULT false")
    /**
     * Indica si la cuenta del usuario está bloqueada.
     */
    private boolean locked;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private RoleJpaEntity roles = new RoleJpaEntity();

    @Column(columnDefinition = "int DEFAULT 0")
    private int failedAttemps;

    public UserJpaEntity() {
    }

    public UserJpaEntity(Long id, String username, String password, String fullName, String firstName,
                            String lastName, String email,
                            boolean enabled, boolean locked, LocalDateTime createdAt, LocalDateTime updatedAt,
                            RoleJpaEntity roles) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = enabled;
        this.locked = locked;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.roles = roles;
        this.failedAttemps = failedAttemps;
    }

    /**
     * Obtiene el identificador del usuario.
     *
     * @return Identificador del usuario.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del usuario.
     *
     * @param id Identificador del usuario.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre de usuario.
     *
     * @return Nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Establece el nombre de usuario.
     *
     * @param username Nombre de usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obtiene la contraseña codificada.
     *
     * @return Contraseña codificada.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Establece la contraseña codificada.
     *
     * @param password Contraseña codificada.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obtiene el nombre completo del usuario.
     *
     * @return Nombre completo.
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Establece el nombre completo del usuario.
     *
     * @param fullName Nombre completo.
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public TypeDocumentEntity getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(TypeDocumentEntity typeDocument) {
        this.typeDocument = typeDocument;
    }

    public Long getDocument() {
        return document;
    }

    public void setDocument(Long document) {
        this.document = document;
    }

    /**
     * Obtiene el primer nombre del usuario.
     *
     * @return Primer nombre.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Establece el primer nombre del usuario.
     *
     * @param firstName Primer nombre.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Obtiene el apellido del usuario.
     *
     * @return Apellido.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Establece el apellido del usuario.
     *
     * @param lastName Apellido.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Obtiene el correo electrónico del usuario.
     *
     * @return Correo electrónico.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Establece el correo electrónico del usuario.
     *
     * @param email Correo electrónico.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Verifica si la cuenta del usuario está habilitada.
     *
     * @return true si está habilitada, false en caso contrario.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Establece si la cuenta del usuario está habilitada.
     *
     * @param enabled true para habilitar, false para deshabilitar.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Verifica si la cuenta del usuario está bloqueada.
     *
     * @return true si está bloqueada, false en caso contrario.
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * Establece si la cuenta del usuario está bloqueada.
     *
     * @param locked true para bloquear, false para desbloquear.
     */
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    /**
     * Obtiene la fecha de creación del usuario.
     *
     * @return Fecha de creación.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación del usuario.
     *
     * @param createdAt Fecha de creación.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obtiene la fecha de última actualización del usuario.
     *
     * @return Fecha de actualización.
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Establece la fecha de última actualización del usuario.
     *
     * @param updatedAt Fecha de actualización.
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Obtiene el conjunto de roles del usuario.
     *
     * @return Conjunto de roles.
     */
    public RoleJpaEntity getRoles() {
        return roles;
    }

    /**
     * Establece el conjunto de roles del usuario.
     *
     * @param roles Conjunto de roles.
     */
    public void setRoles(RoleJpaEntity roles) {
        this.roles = roles;
    }

    public int getFailedAttemps() {
        return failedAttemps;
    }

    public void setFailedAttemps(int failedAttemps) {
        this.failedAttemps = failedAttemps;
    }

    /**
     * Callback JPA que establece las fechas de creación y actualización si no existen.
     */
    @PrePersist
    void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    /**
     * Callback JPA que actualiza la fecha de modificación antes de cada actualización.
     */
    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}




