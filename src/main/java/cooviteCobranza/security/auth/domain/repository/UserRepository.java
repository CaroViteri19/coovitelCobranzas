package cooviteCobranza.security.auth.domain.repository;

import cooviteCobranza.security.auth.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository {

    User create(User user);

    User update(User user);

    User findById(Long id);

    Optional<User> findByEmail(String email);
}
