package coovitelCobranza.security.application.service;

import coovitelCobranza.security.config.SecurityBootstrapProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;

/**
 * Cargador de datos de inicialización para seguridad.
 * Crea el rol de administrador y el usuario administrador por defecto al iniciar la aplicación.
 */
@Component
public class SecurityBootstrapDataLoader implements CommandLineRunner {

    private final SecurityBootstrapProperties bootstrapProperties;
    private final RoleJpaRepository roleRepository;
    private final UserJpaRepository userRepository;

    private static final String ADMIN_PASSWORD_HASH = "$2b$12$fqSw.mgazZMAs12M0ncI1u3ojo8vFT01QKLWWHg/a9Adf/Al5tnou";

    /**
     * Constructor con inyección de dependencias.
     *
     * @param bootstrapProperties Propiedades de configuración del bootstrap.
     * @param roleRepository Repositorio de roles.
     * @param userRepository Repositorio de usuarios.
     */
    public SecurityBootstrapDataLoader(SecurityBootstrapProperties bootstrapProperties,
                                      RoleJpaRepository roleRepository,
                                      UserJpaRepository userRepository) {
        this.bootstrapProperties = bootstrapProperties;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    /**
     * Ejecuta la carga de datos inicial si está habilitada.
     * Crea el rol ADMIN y el usuario administrador si no existen.
     *
     * @param args Argumentos de línea de comandos (no utilizados).
     */
    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapProperties.isEnabled()) {
            return;
        }

        RoleJpaEntity adminRole = roleRepository.findByName("ADMIN")
                .orElseGet(() -> {
                    RoleJpaEntity role = new RoleJpaEntity();
                    role.setName("ADMIN");
                    role.setDescription("System administrator role");
                    role.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(role);
                });

        if (userRepository.existsByUsername(bootstrapProperties.getAdminUsername())) {
            return;
        }

        UserJpaEntity adminUser = new UserJpaEntity();
        String[] nameParts = splitFullName(bootstrapProperties.getAdminFullName());
        adminUser.setUsername(bootstrapProperties.getAdminUsername());
        adminUser.setPassword(ADMIN_PASSWORD_HASH);
        adminUser.setFullName(bootstrapProperties.getAdminFullName());
        adminUser.setFirstName(nameParts[0]);
        adminUser.setLastName(nameParts[1]);
        adminUser.setEmail(null);
        adminUser.setEnabled(true);
        adminUser.setLocked(false);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());
        adminUser.setRoles(new LinkedHashSet<>());
        adminUser.addRole(adminRole);
        userRepository.save(adminUser);
    }

    /**
     * Divide el nombre completo en primer nombre y apellidos.
     *
     * @param fullName Nombre completo a dividir.
     * @return Array con [primer nombre, apellidos].
     */
    private String[] splitFullName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        if (trimmed.isEmpty()) {
            return new String[]{"System", "Administrator"};
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return new String[]{parts[0], String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length))};
    }
}

