package coovitelCobranza.security.application.service;

import coovitelCobranza.security.config.SecurityBootstrapProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.TypeDocumentEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.TypeDocumentRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - SecurityBootstrapDataLoader")
class SecurityBootstrapDataLoaderTest {

    @Mock
    private RoleJpaRepository roleRepository;

    @Mock
    private TypeDocumentRepository typeDocumentRepository;

    @Mock
    private UserJpaRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private SecurityBootstrapDataLoader loader;

    @BeforeEach
    void setUp() {
        SecurityBootstrapProperties properties = new SecurityBootstrapProperties();
        properties.setEnabled(true);
        properties.setAdminUsername("admin");
        properties.setAdminEmail("admin@coovitel.local");
        properties.setAdminPassword("Admin123!");
        properties.setAdminFullName("System Administrator");
        properties.setAdminDocumentType("CC");
        properties.setAdminDocument(1000000000L);

        loader = new SecurityBootstrapDataLoader(
                properties,
                roleRepository,
                typeDocumentRepository,
                userRepository,
                passwordEncoder
        );
    }

    @Test
    @DisplayName("Bootstrap creates admin role, document type and admin user")
    void bootstrapCreatesAdminUser() {
        when(roleRepository.findByName("ADMINISTRATOR")).thenReturn(Optional.empty());
        when(roleRepository.save(any(RoleJpaEntity.class))).thenAnswer(invocation -> {
            RoleJpaEntity role = invocation.getArgument(0);
            role.setId(1L);
            return role;
        });
        when(typeDocumentRepository.findByAbbreviationIgnoreCase("CC")).thenReturn(Optional.empty());
        when(typeDocumentRepository.save(any(TypeDocumentEntity.class))).thenAnswer(invocation -> {
            TypeDocumentEntity documentType = invocation.getArgument(0);
            documentType.setId(2L);
            return documentType;
        });
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@coovitel.local")).thenReturn(false);
        when(passwordEncoder.encode("Admin123!")).thenReturn("encoded-password");
        when(userRepository.save(any(UserJpaEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        loader.run();

        ArgumentCaptor<UserJpaEntity> userCaptor = ArgumentCaptor.forClass(UserJpaEntity.class);
        verify(userRepository).save(userCaptor.capture());

        UserJpaEntity savedUser = userCaptor.getValue();
        assertEquals("admin", savedUser.getUsername());
        assertEquals("admin@coovitel.local", savedUser.getEmail());
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals(1000000000L, savedUser.getDocument());
        assertNotNull(savedUser.getTypeDocument());
        assertEquals("CC", savedUser.getTypeDocument().getAbbreviation());
        assertNotNull(savedUser.getRoles());
        assertEquals("ADMINISTRATOR", savedUser.getRoles().getName());
        assertTrue(savedUser.isEnabled());
    }

    @Test
    @DisplayName("Bootstrap does nothing when admin already exists")
    void bootstrapSkipsExistingAdmin() {
        when(roleRepository.findByName("ADMINISTRATOR")).thenReturn(Optional.of(new RoleJpaEntity(1L, "ADMINISTRATOR", "Acceso total al sistema", null)));
        when(typeDocumentRepository.findByAbbreviationIgnoreCase("CC")).thenReturn(Optional.of(new TypeDocumentEntity(2L, "Cedula", "CC")));
        when(userRepository.existsByUsername("admin")).thenReturn(true);

        loader.run();

        verify(userRepository, never()).save(any(UserJpaEntity.class));
        verify(roleRepository, never()).save(any(RoleJpaEntity.class));
        verify(typeDocumentRepository, never()).save(any(TypeDocumentEntity.class));
    }
}




