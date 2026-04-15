package coovitelCobranza.security.application.service;

import coovitelCobranza.security.config.SecurityBootstrapProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.TypeDocumentEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.TypeDocumentRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Locale;

/**
 * Inicializa datos básicos de seguridad al arrancar la aplicación.
 * Crea el rol ADMIN, el tipo de documento base y un usuario administrador genérico.
 */
@Component
@ConditionalOnProperty(prefix = "app.security.bootstrap", name = "enabled", havingValue = "true")
public class SecurityBootstrapDataLoader implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SecurityBootstrapDataLoader.class);

    private final SecurityBootstrapProperties bootstrapProperties;
    private final RoleJpaRepository roleRepository;
    private final TypeDocumentRepository typeDocumentRepository;
    private final UserJpaRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public SecurityBootstrapDataLoader(SecurityBootstrapProperties bootstrapProperties,
                                       RoleJpaRepository roleRepository,
                                       TypeDocumentRepository typeDocumentRepository,
                                       UserJpaRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        this.bootstrapProperties = bootstrapProperties;
        this.roleRepository = roleRepository;
        this.typeDocumentRepository = typeDocumentRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapProperties.isEnabled()) {
            return;
        }

        RoleJpaEntity adminRole = ensureAdminRole();
        TypeDocumentEntity adminDocumentType = ensureAdminDocumentType();
        ensureAdminUser(adminRole, adminDocumentType);
    }

    private RoleJpaEntity ensureAdminRole() {
        return roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    RoleJpaEntity role = new RoleJpaEntity();
                    role.setName("ADMIN");
                    role.setDescription("Acceso total al sistema");
                    role.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(role);
                });
    }

    private TypeDocumentEntity ensureAdminDocumentType() {
        String abbreviation = bootstrapProperties.getAdminDocumentType() == null
                ? "CC"
                : bootstrapProperties.getAdminDocumentType().trim().toUpperCase(Locale.ROOT);
        return typeDocumentRepository.findByAbbreviationIgnoreCase(abbreviation)
                .orElseGet(() -> {
                    TypeDocumentEntity documentType = new TypeDocumentEntity();
                    documentType.setAbbreviation(abbreviation);
                    documentType.setDescription("Documento de identidad");
                    return typeDocumentRepository.save(documentType);
                });
    }

    private void ensureAdminUser(RoleJpaEntity adminRole, TypeDocumentEntity adminDocumentType) {
        String username = normalize(bootstrapProperties.getAdminUsername());
        String email = normalize(bootstrapProperties.getAdminEmail());

        if (userRepository.existsByUsername(username) || userRepository.existsByEmail(email)) {
            return;
        }

        String fullName = bootstrapProperties.getAdminFullName() == null ? "System Administrator" : bootstrapProperties.getAdminFullName().trim();
        String[] nameParts = splitFullName(fullName);

        UserJpaEntity admin = new UserJpaEntity();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPassword(passwordEncoder.encode(bootstrapProperties.getAdminPassword()));
        admin.setFullName(fullName);
        admin.setFirstName(nameParts[0]);
        admin.setLastName(nameParts[1]);
        admin.setTypeDocument(adminDocumentType);
        admin.setDocument(bootstrapProperties.getAdminDocument());
        admin.setEnabled(true);
        admin.setLocked(false);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        admin.setRoles(adminRole);

        try {
            userRepository.save(admin);
            log.info("Usuario administrador genérico creado: {} ({})", username, email);
        } catch (DataIntegrityViolationException ex) {
            log.warn("No se pudo crear el usuario administrador genérico: {}", ex.getMessage());
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private String[] splitFullName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isBlank()) {
            return new String[]{"System", "Administrator"};
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return new String[]{parts[0], String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length))};
    }
}



