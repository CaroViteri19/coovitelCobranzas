package cooviteCobranza.security.auth.infrastructure.persistence.repository;

import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.repository.UserRepository;
import cooviteCobranza.security.auth.infrastructure.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRepositoryImp implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    public UserRepositoryImp(JpaUserRepository jpaUserRepository) {
        this.jpaUserRepository = jpaUserRepository;
    }

    @Override
    public User create(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity saved = jpaUserRepository.save(userEntity);
        return toDomain(saved);
    }

    @Override
    public User update(User user) {
        UserEntity userEntity = toEntity(user);
        UserEntity saved = jpaUserRepository.save(userEntity);
        return toDomain(saved);
    }

    @Override
    public User findById(Long id) {
        return jpaUserRepository.findById(id)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email)
                .map(this::toDomain);
    }

    User toDomain(UserEntity userEntity){
        return new User(
                userEntity.getIdUser(),
                userEntity.getName(),
                userEntity.getLastname(),
                userEntity.getEmail(),
                userEntity.getPassword(),
                userEntity.getRole()
        );
    }

    UserEntity toEntity(User user){
        return  new UserEntity(
                user.getIdUser(),
                user.getName(),
                user.getLastname(),
                user.getEmail(),
                user.getPassword(),
                user.getRole()
        );
    }
}
