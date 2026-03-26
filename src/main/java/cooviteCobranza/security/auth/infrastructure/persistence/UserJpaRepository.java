package cooviteCobranza.security.auth.infrastructure.persistence;

import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.repository.UserRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA Repository Interface
 * 
 * Esta interfaz extiende JpaRepository para proporcionar
 * operaciones CRUD básicas a Spring Data
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {
    Optional<UserJpaEntity> findByEmail(String email);
    Optional<UserJpaEntity> findByName(String username);
    List<UserJpaEntity> findByActiveTrue();
    boolean existsByEmail(String email);
    boolean existsByName(String username);
    long countByActiveTrue();
}

