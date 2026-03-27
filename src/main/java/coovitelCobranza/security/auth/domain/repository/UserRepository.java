package coovitelCobranza.security.auth.domain.repository;

import coovitelCobranza.security.auth.domain.model.User;

import java.util.Optional;
import java.util.List;

/**
 * Domain Repository: UserRepository
 * 
 * Principios DDD:
 * 1. Define el contrato para persistencia del agregado User
 * 2. Los métodos reflejan la Ubiquitous Language del dominio
 * 3. No expone detalles de implementación (JPA, queries, etc.)
 * 4. La interfaz pertenece al dominio, la implementación pertenece a infraestructura
 */
public interface UserRepository {

    /**
     * Guardar un usuario (nuevo o actualizado)
     * Este método encapsula tanto create como update
     */
    User save(User user);

    /**
     * Obtener un usuario por su identificador único
     */
    Optional<User> findById(Long id);

    /**
     * Obtener un usuario por su email (email es único)
     */
    Optional<User> findByEmail(String email);

    /**
     * Obtener un usuario por su nombre de usuario
     */
    Optional<User> findByUsername(String username);

    /**
     * Obtener todos los usuarios activos
     */
    List<User> findAllActive();

    /**
     * Eliminar un usuario (soft delete o hard delete, según la estrategia)
     */
    void delete(User user);

    /**
     * Eliminar un usuario por su identificador
     */
    void deleteById(Long id);

    /**
     * Verificar si existe un usuario con un email específico
     */
    boolean existsByEmail(String email);

    /**
     * Verificar si existe un usuario con un nombre de usuario específico
     */
    boolean existsByUsername(String username);

    /**
     * Obtener el total de usuarios activos
     */
    long countActive();
}
