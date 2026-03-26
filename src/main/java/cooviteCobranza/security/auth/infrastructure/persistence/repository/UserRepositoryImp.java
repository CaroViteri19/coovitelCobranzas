package cooviteCobranza.security.auth.domain.repository;

import cooviteCobranza.security.auth.infrastructure.persistence.repository.JpaUserRepository;
import org.springframework.stereotype.Component;

@Component
public class UserRepositoryImp implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImp(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }
}
