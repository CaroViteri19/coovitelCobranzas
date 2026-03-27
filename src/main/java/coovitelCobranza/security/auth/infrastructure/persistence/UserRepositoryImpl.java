package coovitelCobranza.security.auth.infrastructure.persistence;

import coovitelCobranza.security.auth.domain.model.User;
import coovitelCobranza.security.auth.domain.repository.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementation: UserRepositoryImpl
 * 
 * Principios DDD:
 * 1. Esta clase implementa la interfaz del dominio (UserRepository)
 * 2. Encapsula los detalles de persistencia (JPA)
 * 3. Realiza conversión entre objetos de dominio y de persistencia
 * 4. El dominio NO conoce sobre JPA, SQL, o Spring Data
 * 
 * Patrón: Repository Adapter
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    
    private final UserJpaRepository jpaRepository;
    
    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    /**
     * Guardar un usuario (insert o update)
     * 
     * Flujo:
     * 1. Recibe User (objeto de dominio)
     * 2. Convierte a UserJpaEntity
     * 3. Persiste en BD
     * 4. Convierte resultado a User (dominio)
     * 5. Retorna User
     */
    @Override
    public User save(User user) {
        UserJpaEntity jpaEntity = UserJpaEntity.fromDomain(user);
        UserJpaEntity saved = jpaRepository.save(jpaEntity);
        return saved.toDomain();
    }
    
    /**
     * Buscar usuario por ID
     */
    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id)
                .map(UserJpaEntity::toDomain);
    }
    
    /**
     * Buscar usuario por email
     */
    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(UserJpaEntity::toDomain);
    }
    
    /**
     * Buscar usuario por nombre de usuario (username)
     */
    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByName(username)
                .map(UserJpaEntity::toDomain);
    }
    
    /**
     * Obtener todos los usuarios activos
     */
    @Override
    public List<User> findAllActive() {
        return jpaRepository.findByActiveTrue().stream()
                .map(UserJpaEntity::toDomain)
                .toList();
    }
    
    /**
     * Eliminar un usuario
     */
    @Override
    public void delete(User user) {
        jpaRepository.deleteById(user.getIdUser());
    }
    
    /**
     * Eliminar usuario por ID
     */
    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }
    
    /**
     * Verificar si existe email
     */
    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
    
    /**
     * Verificar si existe username
     */
    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByName(username);
    }
    
    /**
     * Contar usuarios activos
     */
    @Override
    public long countActive() {
        return jpaRepository.countByActiveTrue();
    }
}

