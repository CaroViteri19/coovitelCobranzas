package coovitelCobranza.security.application.service;

import coovitelCobranza.security.config.SecurityBootstrapProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Cargador de datos de inicialización para seguridad.
 * Crea roles y usuarios de prueba al iniciar la aplicación.
 * Los usuarios están disponibles inmediatamente después de cada reinicio.
 */
@Component
public class SecurityBootstrapDataLoader implements CommandLineRunner {

    private final SecurityBootstrapProperties bootstrapProperties;
    private final RoleJpaRepository           roleRepository;
    private final UserJpaRepository           userRepository;
    private final PasswordEncoder             passwordEncoder;

    public SecurityBootstrapDataLoader(SecurityBootstrapProperties bootstrapProperties,
                                       RoleJpaRepository roleRepository,
                                       UserJpaRepository userRepository,
                                       PasswordEncoder passwordEncoder) {
        this.bootstrapProperties = bootstrapProperties;
        this.roleRepository      = roleRepository;
        this.userRepository      = userRepository;
        this.passwordEncoder     = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (!bootstrapProperties.isEnabled()) {
            return;
        }

        // ── 1. Crear los 4 roles del sistema ───────────────────────────────────
        RoleJpaEntity adminRole    = createRoleIfNotExists("ADMINISTRADOR",
                "Acceso total al sistema. Gestiona usuarios, configuracion y todos los modulos.");
        RoleJpaEntity superRole    = createRoleIfNotExists("SUPERVISOR",
                "Supervisa agentes y casos. Acceso de escritura en gestion, recaudo y orquestacion.");
        RoleJpaEntity agenteRole   = createRoleIfNotExists("AGENTE",
                "Ejecuta gestiones de cobranza. Acceso a casos, interacciones y recaudo.");
        RoleJpaEntity auditorRole  = createRoleIfNotExists("AUDITOR",
                "Revision y auditoria. Acceso de solo lectura en todos los modulos.");

        // ── 2. Crear usuarios de prueba por cada rol ───────────────────────────
        // Todos usan contraseñas que cumplen la política: ≥12 chars + 1 especial
        createUserIfNotExists("admin",      "admin@coovitel.co",      "Admin@coovitel1!",  "System Administrator",  "System",   "Administrator", adminRole);
        createUserIfNotExists("supervisor", "supervisor@coovitel.co", "Super@coovitel1!",  "Laura Rodriguez",       "Laura",    "Rodriguez",     superRole);
        createUserIfNotExists("agente01",   "agente01@coovitel.co",   "Agente@coovitel1!", "Agente Primero",        "Agente",   "Primero",       agenteRole);
        createUserIfNotExists("auditor",    "auditor@coovitel.co",    "Audit@coovitel1!",  "Carlos Mejia",          "Carlos",   "Mejia",         auditorRole);

        System.out.println("""
            ╔══════════════════════════════════════════════════════════════╗
            ║              USUARIOS DE PRUEBA DISPONIBLES                  ║
            ╠══════════════╦══════════════════════════╦═══════════════════╣
            ║ ROL          ║ EMAIL                    ║ CONTRASEÑA        ║
            ╠══════════════╬══════════════════════════╬═══════════════════╣
            ║ ADMINISTRADOR║ admin@coovitel.co        ║ Admin@coovitel1!  ║
            ║ SUPERVISOR   ║ supervisor@coovitel.co   ║ Super@coovitel1!  ║
            ║ AGENTE       ║ agente01@coovitel.co     ║ Agente@coovitel1! ║
            ║ AUDITOR      ║ auditor@coovitel.co      ║ Audit@coovitel1!  ║
            ╚══════════════╩══════════════════════════╩═══════════════════╝
            """);
    }

    // ── helpers ────────────────────────────────────────────────────────────────

    private void createUserIfNotExists(String username, String email, String rawPassword,
                                       String fullName, String firstName, String lastName,
                                       RoleJpaEntity role) {
        if (userRepository.existsByUsername(username)) {
            return;
        }
        UserJpaEntity user = new UserJpaEntity();
        user.setUsername(username);
        user.setEmail(email.toLowerCase());
        user.setPassword(passwordEncoder.encode(rawPassword));   // hash BCrypt $2a$ generado en tiempo real
        user.setFullName(fullName);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(role);
        userRepository.save(user);
    }

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
}
