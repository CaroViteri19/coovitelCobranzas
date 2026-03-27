package cooviteCobranza.security.auth.infrastructure.persistence;

import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.model.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JPA Entity: UserJpaEntity
 * 
 * Principios DDD:
 * 1. Este es un objeto de persistencia, NO un objeto de dominio
 * 2. Contiene anotaciones JPA
 * 3. Se utiliza como adaptador entre la base de datos y el modelo de dominio
 * 4. Las conversiones User <-> UserJpaEntity ocurren aquí (Mapping)
 */
@Entity
@Table(name = "users")
@Getter
@Setter
public class UserJpaEntity {
    
    // Constructor sin argumentos
    public UserJpaEntity() {
        this.roles = new HashSet<>();
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long idUser;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false)
    private String lastname;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "id_user"),
        inverseJoinColumns = @JoinColumn(name = "id_role")
    )
    private Set<RoleJpaEntity> roles = new HashSet<>();
    
    public User toDomain() {
        Set<Role> domainRoles = this.roles.stream()
                .map(RoleJpaEntity::toDomain)
                .collect(Collectors.toSet());
        
        return User.restore(
                this.idUser,
                this.name,
                this.lastname,
                this.email,
                this.password,
                domainRoles,
                this.active,
                this.createdAt,
                this.updatedAt,
                this.lastLogin
        );
    }
    
    public static UserJpaEntity fromDomain(User domainUser) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setIdUser(domainUser.getIdUser());
        entity.setName(domainUser.getName());
        entity.setLastname(domainUser.getLastname());
        entity.setEmail(domainUser.getEmail());
        entity.setPassword(domainUser.getPassword());
        entity.setActive(domainUser.getActive());
        entity.setCreatedAt(domainUser.getCreatedAt());
        entity.setUpdatedAt(domainUser.getUpdatedAt());
        entity.setLastLogin(domainUser.getLastLogin());
        
        Set<RoleJpaEntity> jpaRoles = domainUser.getRoles().stream()
                .map(RoleJpaEntity::fromDomain)
                .collect(Collectors.toSet());
        entity.setRoles(jpaRoles);
        
        return entity;
    }
    
    public void updateFromDomain(User domainUser) {
        this.name = domainUser.getName();
        this.lastname = domainUser.getLastname();
        this.email = domainUser.getEmail();
        this.password = domainUser.getPassword();
        this.active = domainUser.getActive();
        this.updatedAt = domainUser.getUpdatedAt();
        this.lastLogin = domainUser.getLastLogin();
        
        Set<RoleJpaEntity> jpaRoles = domainUser.getRoles().stream()
                .map(RoleJpaEntity::fromDomain)
                .collect(Collectors.toSet());
        this.roles = jpaRoles;
    }
}








