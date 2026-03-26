package cooviteCobranza.security.auth.infrastructure.persistence;

import cooviteCobranza.security.auth.domain.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * JPA Entity: RoleJpaEntity
 * 
 * Adaptador de persistencia para el objeto de dominio Role
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column
    private String description;
    
    /**
     * Convertir de RoleJpaEntity (Persistencia) a Role (Dominio)
     */
    public Role toDomain() {
        return Role.restore(this.idRole, this.name, this.description);
    }
    
    /**
     * Convertir de Role (Dominio) a RoleJpaEntity (Persistencia)
     */
    public static RoleJpaEntity fromDomain(Role domainRole) {
        RoleJpaEntity entity = new RoleJpaEntity();
        entity.setIdRole(domainRole.getIdRole());
        entity.setName(domainRole.getName());
        entity.setDescription(domainRole.getDescription());
        return entity;
    }
}


