package coovitelCobranza.security.application.service;

import coovitelCobranza.security.application.dto.UpdateUserRequest;
import coovitelCobranza.security.application.dto.UpdateUserStatusRequest;
import coovitelCobranza.security.application.dto.UserSummaryResponse;
import coovitelCobranza.config.exception.ConflictException;
import coovitelCobranza.config.exception.ResourceNotFoundException;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

/**
 * Servicio de administracion de usuarios para endpoints de Settings.
 */
@Service
public class UserManagementService {

    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;

    public UserManagementService(UserJpaRepository userRepository,
                                 RoleJpaRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Transactional(readOnly = true)
    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(user -> new UserSummaryResponse(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRoles() != null ? user.getRoles().getName() : "AGENTE",
                        user.getUpdatedAt(),
                        user.isEnabled() ? "Active" : "Inactive"
                ))
                .toList();
    }

    @Transactional
    public void updateUserStatus(UpdateUserStatusRequest request) {
        UserJpaEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setEnabled(request.enabled());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public void updateUser(UpdateUserRequest request) {
        UserJpaEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmailAndIdNot(normalizedEmail, user.getId())) {
            throw new ConflictException("Email already exists");
        }

        user.setFullName(request.fullName().trim());
        String[] nameParts = splitFullName(request.fullName());
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts[1]);
        user.setEmail(normalizedEmail);

        if (request.role() != null && !request.role().isBlank()) {
            String roleName = request.role().trim().toUpperCase(Locale.ROOT);
            RoleJpaEntity role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new ResourceNotFoundException("Role not found"));
            user.setRoles(role);
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private String[] splitFullName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return new String[]{"User", "User"};
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return new String[]{parts[0], String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length))};
    }
}


