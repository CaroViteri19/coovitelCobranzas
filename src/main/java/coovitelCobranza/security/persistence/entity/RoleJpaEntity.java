package coovitelCobranza.security.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad JPA que representa un rol en el sistema.
 * Almacena información sobre los permisos y roles disponibles para los usuarios.
 */
@Entity
@Table(name = "roles")
public class RoleJpaEntity {

    /**
     * Identificador único del rol.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del rol (único, máximo 50 caracteres).
     */
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    /**
     * Descripción del rol (máximo 150 caracteres).
     */
    @Column(length = 150)
    private String description;

    /**
     * Fecha y hora de creación del rol.
     */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /**
     * Constructor sin argumentos.
     */
    public RoleJpaEntity() {
    }

    /**
     * Constructor con argumentos.
     *
     * @param id Identificador del rol.
     * @param name Nombre del rol.
     * @param description Descripción del rol.
     * @param createdAt Fecha de creación.
     */
    public RoleJpaEntity(Long id, String name, String description, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
    }

    /**
     * Obtiene el identificador del rol.
     *
     * @return Identificador del rol.
     */
    public Long getId() {
        return id;
    }

    /**
     * Establece el identificador del rol.
     *
     * @param id Identificador del rol.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obtiene el nombre del rol.
     *
     * @return Nombre del rol.
     */
    public String getName() {
        return name;
    }

    /**
     * Establece el nombre del rol.
     *
     * @param name Nombre del rol.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Obtiene la descripción del rol.
     *
     * @return Descripción del rol.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Establece la descripción del rol.
     *
     * @param description Descripción del rol.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Obtiene la fecha de creación del rol.
     *
     * @return Fecha de creación.
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Establece la fecha de creación del rol.
     *
     * @param createdAt Fecha de creación.
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Callback JPA que establece la fecha de creación si no existe.
     */
    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RoleJpaEntity that = (RoleJpaEntity) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}



