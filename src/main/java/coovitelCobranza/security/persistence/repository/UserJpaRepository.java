package coovitelCobranza.security.persistence.repository;

import java.util.Optional;

import coovitelCobranza.security.persistence.entity.UserJpaEntity;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repositorio JPA para operaciones de persistencia de la entidad UserJpaEntity.
 * Proporciona métodos para consultar y manipular datos de usuarios en la base de datos.
 */
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     *
     * @param username Nombre de usuario a buscar.
     * @return Optional conteniendo el usuario si existe, vacío en caso contrario.
     */
    Optional<UserJpaEntity> findByUsername(String username);

    /**
     * Verifica si un usuario existe por nombre de usuario.
     *
     * @param username Nombre de usuario a verificar.
     * @return true si el usuario existe, false en caso contrario.
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si un usuario existe por correo electrónico.
     *
     * @param email Correo electrónico a verificar.
     * @return true si el correo existe, false en caso contrario.
     */
    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);

    UserJpaRepository findByUsernameAndPassword(String username, String password);
    Optional<UserJpaEntity> findByEmail(String email);

}


