package coovitelCobranza.security.application.service;

import coovitelCobranza.security.application.dto.UserResponse;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    @Autowired
    private UserJpaRepository userJpaRepository;

    public List<UserResponse> getAllUsers() {
        try {
            List<UserJpaEntity> users = userJpaRepository.findAll();
            return users.stream().map(user -> new UserResponse(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRoles() != null ? user.getRoles().getName() : "ROLE_USER",
                    user.getUpdatedAt(),
                    user.isEnabled() ? "Active" : "Inactive"
            )).toList();
        } catch (Exception e) {
            // Manejo de excepciones, por ejemplo, loguear el error
            System.err.println("Error al obtener usuarios: " + e.getMessage());
            return List.of(); // Retorna una lista vacía en caso de error
        }

    }
}
