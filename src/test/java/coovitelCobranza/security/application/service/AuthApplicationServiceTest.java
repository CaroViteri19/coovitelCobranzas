package coovitelCobranza.security.application.service;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import coovitelCobranza.security.application.dto.RegisterUserRequest;
import coovitelCobranza.security.application.dto.RegisterUserResponse;
import coovitelCobranza.security.application.exception.UserAlreadyExistsException;
import coovitelCobranza.security.config.JwtProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - AuthApplicationService")
class AuthApplicationServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private RoleJpaRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuditService auditService;

    private AuthApplicationService service;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setIssuer("test-issuer");
        jwtProperties.setSecret("test-secret-key-with-at-least-32-characters");
        jwtProperties.setExpirationMinutes(60);

        service = new AuthApplicationService(
                authenticationManager,
                jwtEncoder,
                jwtProperties,
                userRepository,
                roleRepository,
                passwordEncoder,
                auditService
        );
    }

    @Test
    @DisplayName("Register creates user with USER role and encoded password")
    void registerSuccess() {
        RegisterUserRequest request = new RegisterUserRequest(
                "newuser",
                "Pass12345!@#",
                "New User",
                "new.user@test.com",
                1L
        );

        RoleJpaEntity userRole = new RoleJpaEntity(1L, "AGENTE", "Default role", LocalDateTime.now());

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new.user@test.com")).thenReturn(false);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(userRole));
        when(passwordEncoder.encode("Pass12345!@#")).thenReturn("encoded-password");
        when(userRepository.save(any(UserJpaEntity.class))).thenAnswer(invocation -> {
            UserJpaEntity user = invocation.getArgument(0);
            user.setId(10L);
            return user;
        });

        RegisterUserResponse response = service.register(request);

        assertEquals(10L, response.id());
        assertEquals("newuser", response.username());
        assertEquals("new.user@test.com", response.email());
        assertTrue(response.roles().contains("AGENTE"));
        assertTrue(response.enabled());
    }

    @Test
    @DisplayName("Register fails when username already exists")
    void registerFailsOnDuplicateUsername() {
        RegisterUserRequest request = new RegisterUserRequest(
                "existing",
                "Pass12345!@#",
                "Existing User",
                "existing@test.com",
                1L
        );

        when(userRepository.existsByUsername("existing")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> service.register(request));
    }

    @Test
    @DisplayName("Register creates USER role when it does not exist")
    void registerCreatesDefaultRoleWhenMissing() {
        RegisterUserRequest request = new RegisterUserRequest(
                "another",
                "Pass12345!@#",
                "Another User",
                "another@test.com",
                2L
        );

        RoleJpaEntity createdRole = new RoleJpaEntity(2L, "AGENTE", "Default platform user role", LocalDateTime.now());

        when(userRepository.existsByUsername("another")).thenReturn(false);
        when(userRepository.existsByEmail("another@test.com")).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(createdRole));
        when(passwordEncoder.encode("Pass12345!@#")).thenReturn("encoded-password");
        when(userRepository.save(any(UserJpaEntity.class))).thenAnswer(invocation -> {
            UserJpaEntity user = invocation.getArgument(0);
            user.setId(11L);
            user.setRoles(new RoleJpaEntity());
            return user;
        });

        RegisterUserResponse response = service.register(request);

        assertEquals(11L, response.id());
        assertTrue(response.roles().contains("AGENTE"));
    }
}

