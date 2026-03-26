package cooviteCobranza.security.auth.domain.model;

import java.util.Objects;

/**
 * Domain Model: Role (Value Object / Entity)
 * 
 * Principios DDD aplicados:
 * 1. Encapsula el concepto de un rol en el dominio
 * 2. No tiene anotaciones de persistencia
 * 3. Contiene validaciones de negocio
 * 4. Los roles son identificados por su nombre (identidad única)
 * 
 * Patrones aplicados:
 * - Value Object: Los roles son identificados por su nombre
 * - Immutability: Una vez creado, no cambia
 */
public class Role {
    
    private final Long idRole;
    private final String name;
    private final String description;

    // Constructor privado
    private Role(Long idRole, String name, String description) {
        validateName(name);
        this.idRole = idRole;
        this.name = name;
        this.description = description;
    }

    /**
     * Factory method para crear un nuevo rol
     */
    public static Role create(String name, String description) {
        return new Role(null, name, description);
    }

    /**
     * Factory method para reconstruir desde persistencia
     */
    public static Role restore(Long idRole, String name, String description) {
        return new Role(idRole, name, description);
    }

    /**
     * Validación del nombre del rol
     */
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del rol no puede estar vacío");
        }
        // Los roles deben tener prefijo 'ROLE_' por convención de Spring Security
        if (!name.matches("^ROLE_[A-Z_]+$")) {
            throw new IllegalArgumentException("El rol debe estar en mayúsculas y empezar con 'ROLE_'. Ejemplo: ROLE_ADMIN");
        }
    }

    /**
     * Obtener el permiso base del rol (formato sin 'ROLE_')
     */
    public String getBasePermission() {
        return name.replace("ROLE_", "");
    }

    // Getters
    
    public Long getIdRole() {
        return idRole;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    // Métodos técnicos (igualdad basada en nombre)
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return "Role{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
