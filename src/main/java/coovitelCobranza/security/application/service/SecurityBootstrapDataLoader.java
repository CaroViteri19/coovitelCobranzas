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

        // Crear los 4 roles del sistema si no existen
        RoleJpaEntity adminRole = createRoleIfNotExists("ADMINISTRADOR", "Acceso total al sistema. Gestiona usuarios, configuracion y todos los modulos.");
        createRoleIfNotExists("SUPERVISOR",    "Supervisa agentes y casos. Acceso de escritura en gestion, recaudo y orquestacion.");
        createRoleIfNotExists("AGENTE",        "Ejecuta gestiones de cobranza. Acceso a casos, interacciones y recaudo.");
        createRoleIfNotExists("AUDITOR",       "Revision y auditoria. Acceso de solo lectura en todos los modulos.");

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
        adminUser.setRoles(adminRole);
        userRepository.save(adminUser);
    }

    /**
     * Crea un rol si no existe en la base de datos.
     *
     * @param name nombre del rol
     * @param description descripcion del rol
     * @return el rol existente o el recien creado
     */
    private RoleJpaEntity createRoleIfNotExists(String name, String description) {
        return roleRepository.findByName(name)
                .orElseGet(() -> {
                    RoleJpaEntity role = new RoleJpaEntity();
                    role.setName(name);
                    role.setDescription(description);
                    role.setCreatedAt(LocalDateTime.now());
                    return roleRepository.save(role);
                });
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

