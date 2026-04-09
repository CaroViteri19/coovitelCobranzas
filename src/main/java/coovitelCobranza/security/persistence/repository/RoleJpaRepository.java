package coovitelCobranza.security.persistence.repository;

import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repositorio JPA para operaciones de persistencia de la entidad RoleJpaEntity.
 * Proporciona métodos para consultar y manipular datos de roles en la base de datos.
 */
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, Long> {

    /**
     * Busca un rol por su nombre.
     *
     * @param name Nombre del rol a buscar.
     * @return Optional conteniendo el rol si existe, vacío en caso contrario.
     */
    Optional<RoleJpaEntity> findByName(String name);

    /**
     * Verifica si un rol existe por nombre.
     *
     * @param name Nombre del rol a verificar.
     * @return true si el rol existe, false en caso contrario.
     */
    boolean existsByName(String name);
}


