package cooviteCobranza.security.auth.domain.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Domain Model: User (Aggregate Root)
 * 
 * Principios DDD aplicados:
 * 1. Es una Entidad de Dominio (tiene identidad única: idUser)
 * 2. Contiene lógica de negocio relacionada con usuarios
 * 3. No tiene anotaciones JPA (la persistencia es transparente)
 * 4. Las reglas de negocio están encapsuladas aquí
 * 5. Los cambios de estado se hacen a través de métodos específicos, no con setters
 */
public class User {
    
    private final Long idUser;
    private String name;
    private String lastname;
    private String email;
    private String password;
    private Set<Role> roles;
    private Boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLogin;

    // Constructor privado para crear instancias controladas
    private User(Long idUser, String name, String lastname, String email, String password, Set<Role> roles) {
        validateEmail(email);
        validatePassword(password);
        validateName(name, lastname);
        
        this.idUser = idUser;
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;
        this.roles = roles != null ? new HashSet<>(roles) : new HashSet<>();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor para crear nuevo usuario (patrón Factory Method)
    public static User create(String name, String lastname, String email, String password) {
        return new User(null, name, lastname, email, password, new HashSet<>());
    }

    // Constructor para reconstruir desde persistencia
    public static User restore(Long idUser, String name, String lastname, String email, String password, Set<Role> roles, Boolean active, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime lastLogin) {
        User user = new User(idUser, name, lastname, email, password, roles);
        user.active = active != null ? active : true;
        user.createdAt = createdAt;
        user.updatedAt = updatedAt;
        user.lastLogin = lastLogin;
        return user;
    }

    // Métodos de negocio (Ubiquitous Language)
    
    /**
     * Registrar un nuevo login del usuario
     */
    public void recordLogin() {
        if (!isActive()) {
            throw new IllegalStateException("No se puede registrar login para un usuario inactivo");
        }
        this.lastLogin = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cambiar contraseña del usuario
     */
    public void changePassword(String newPassword) {
        validatePassword(newPassword);
        if (Objects.equals(this.password, newPassword)) {
            throw new IllegalArgumentException("La nueva contraseña debe ser diferente de la actual");
        }
        this.password = newPassword;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verificar si el usuario está activo
     */
    public boolean isActive() {
        return Boolean.TRUE.equals(this.active);
    }

    /**
     * Activar usuario
     */
    public void activate() {
        if (isActive()) {
            throw new IllegalStateException("El usuario ya está activo");
        }
        this.active = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Desactivar usuario
     */
    public void deactivate() {
        if (!isActive()) {
            throw new IllegalStateException("El usuario ya está inactivo");
        }
        this.active = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Verificar si el usuario tiene un rol específico
     */
    public boolean hasRole(String roleName) {
        return this.roles.stream()
                .anyMatch(role -> role.getName().equals(roleName));
    }

    /**
     * Asignar un rol al usuario
     */
    public void assignRole(Role role) {
        Objects.requireNonNull(role, "El rol no puede ser nulo");
        if (hasRole(role.getName())) {
            throw new IllegalArgumentException("El usuario ya tiene asignado el rol: " + role.getName());
        }
        this.roles.add(role);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Remover un rol del usuario
     */
    public void removeRole(Role role) {
        Objects.requireNonNull(role, "El rol no puede ser nulo");
        boolean removed = this.roles.remove(role);
        if (!removed) {
            throw new IllegalArgumentException("El usuario no tiene el rol: " + role.getName());
        }
        this.updatedAt = LocalDateTime.now();
    }

    // Validaciones privadas
    
    private void validateEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacío");
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("El email no tiene un formato válido");
        }
    }

    private void validatePassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        if (password.length() < 8) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        }
    }

    private void validateName(String name, String lastname) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        if (lastname == null || lastname.trim().isEmpty()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío");
        }
    }

    // Getters (sin setters, la mutación es a través de métodos de negocio)
    
    public Long getIdUser() {
        return idUser;
    }

    public String getName() {
        return name;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Boolean getActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    // Métodos técnicos (igualdad basada en identidad)
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(idUser, user.idUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser=" + idUser +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", active=" + active +
                ", rolesCount=" + roles.size() +
                '}';
    }
}
