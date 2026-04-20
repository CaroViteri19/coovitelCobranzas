package coovitelCobranza.security.application.service;

import coovitelCobranza.security.application.dto.UpdateUserRequest;
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

    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        try {
            UserJpaEntity user = userJpaRepository.findById(updateUserRequest.userId())
                    .orElseThrow(() -> new RuntimeException("User not found with id: " + updateUserRequest.userId()));

            if (updateUserRequest.email() != null && !updateUserRequest.email().isBlank()) {
                user.setEmail(updateUserRequest.email());
            } else {
                user.setEmail(user.getEmail()); // Mantiene el email actual si no se proporciona uno nuevo
            }

            if (updateUserRequest.enabled() != null || updateUserRequest.enabled() != user.isEnabled()) {
                user.setEnabled(updateUserRequest.enabled());
            }
            user.setEnabled(updateUserRequest.enabled());

            UserJpaEntity updatedUser = userJpaRepository.save(user);
            return new UserResponse(
                    updatedUser.getId(),
                    updatedUser.getFullName(),
                    updatedUser.getEmail(),
                    updatedUser.getRoles() != null ? updatedUser.getRoles().getName() : "ROLE_USER",
                    updatedUser.getUpdatedAt(),
                    updatedUser.isEnabled() ? "Active" : "Inactive"
            );
        } catch (Exception e) {
            // Manejo de excepciones, por ejemplo, loguear el error
            System.err.println("Error al actualizar usuario: " + e.getMessage());
            throw new RuntimeException("Error updating user", e); // O manejarlo de otra forma según la arquitectura
        }
    }
}
