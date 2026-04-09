package coovitelCobranza.security.application.service;

import coovitelCobranza.cobranzas.auditoria.domain.service.AuditService;
import coovitelCobranza.security.application.dto.*;
import coovitelCobranza.security.application.exception.InvalidCredentialsException;
import coovitelCobranza.security.application.exception.UserAlreadyExistsException;
import coovitelCobranza.security.config.JwtProperties;
import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.RoleJpaRepository;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

/**
 * Servicio de aplicación para gestionar la autenticación y registro de usuarios.
 * Maneja el proceso de login, registro y asignación de roles con autenticación basada en JWT.
 */
@Service
public class AuthApplicationService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditService auditService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param authenticationManager Gestor de autenticación.
     * @param jwtEncoder Codificador de JWT.
     * @param jwtProperties Propiedades de configuración de JWT.
     * @param userRepository Repositorio de usuarios.
     * @param roleRepository Repositorio de roles.
     * @param passwordEncoder Codificador de contraseñas.
     */
    public AuthApplicationService(AuthenticationManager authenticationManager,
                                  JwtEncoder jwtEncoder,
                                  JwtProperties jwtProperties,
                                  UserJpaRepository userRepository,
                                  RoleJpaRepository roleRepository,
                                  PasswordEncoder passwordEncoder,
                                  AuditService auditService) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditService = auditService;
    }

    /**
     * Registra un nuevo usuario en el sistema.
     * Verifica la unicidad del usuario y email, codifica la contraseña y asigna el rol por defecto.
     *
     * @param request Solicitud de registro con los datos del usuario.
     * @return Respuesta con la información del usuario creado.
     * @throws UserAlreadyExistsException Si el usuario o email ya existen en el sistema.
     */
    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            // Log failed registration attempt - username already exists
            auditService.registerEvent(
                    "SECURITY",
                    "USER",
                    null,
                    "REGISTRATION_FAILED",
                    request.username(),
                    "ANONYMOUS",
                    "API",
                    "Registration failed: Username already exists",
                    null
            );
            throw new UserAlreadyExistsException("Username already exists");
        }

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
            // Log failed registration attempt - email already exists
            auditService.registerEvent(
                    "SECURITY",
                    "USER",
                    null,
                    "REGISTRATION_FAILED",
                    request.username(),
                    "ANONYMOUS",
                    "API",
                    "Registration failed: Email already exists",
                    null
            );
            throw new UserAlreadyExistsException("Email already exists");
        }

        // Registration uses a default USER role so new accounts are created with minimum permissions.
        RoleJpaEntity defaultRole = roleRepository.findByName("USER")
                .orElseGet(() -> roleRepository.save(new RoleJpaEntity(
                        null,
                        "USER",
                        "Default platform user role",
                        LocalDateTime.now()
                )));

        UserJpaEntity user = new UserJpaEntity();
        String[] nameParts = splitFullName(request.fullName().trim());
        user.setUsername(request.username().trim());

        // Valida que la contraseña tenga minimo 12 caracteres y al menos 1 espcial
        if (request.password().matches("^(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{12,}$")) {
            user.setPassword(passwordEncoder.encode(request.password()));
        } else {
            // Log failed registration attempt - invalid password
            auditService.registerEvent(
                    "SECURITY",
                    "USER",
                    null,
                    "REGISTRATION_FAILED",
                    request.username(),
                    "ANONYMOUS",
                    "API",
                    "Registration failed: Password does not meet complexity requirements",
                    null
            );
            throw new IllegalArgumentException("Password must be at least 12 characters long and contain at least one special character");
        }

        user.setFullName(request.fullName().trim());
        user.setFirstName(nameParts[0]);
        user.setLastName(nameParts[1]);
        user.setEmail(normalizedEmail);
        user.setEnabled(true);
        user.setLocked(false);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setRoles(new LinkedHashSet<>());
        user.addRole(defaultRole);

        UserJpaEntity savedUser = userRepository.save(user);
        List<String> roles = savedUser.getRoles().stream().map(RoleJpaEntity::getName).toList();

        // Log successful registration
        auditService.registerEvent(
                "SECURITY",
                "USER",
                savedUser.getId(),
                "REGISTRATION_SUCCESS",
                savedUser.getUsername(),
                "USER",
                "API",
                "User registered successfully with roles: " + roles,
                null
        );

        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                roles,
                savedUser.isEnabled()
        );
    }

    /**
     * Autentica un usuario y genera un token JWT.
     * Valida las credenciales y crea un token firmado con la información del usuario.
     *
     * @param request Solicitud de login con credenciales.
     * @return Respuesta con el token JWT y datos del usuario autenticado.
     * @throws InvalidCredentialsException Si las credenciales son inválidas.
     */
    public LoginResponse login(LoginRequest request) {
        UserJpaEntity user = userRepository.findByUsername(request.username()).orElseThrow(() -> {
            // Log failed login attempt - user not found
            auditService.registerEvent(
                    "SECURITY",
                    "USER",
                    null,
                    "LOGIN_FAILED",
                    request.username(),
                    "ANONYMOUS",
                    "API",
                    "Login failed: User not found",
                    null
            );
            return new InvalidCredentialsException("Invalid username or password");
        });
        try {
            if (!user.isLocked()) {

                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.username(), request.password()));
                Instant issuedAt = Instant.now();
                Instant expiresAt = issuedAt.plus(jwtProperties.getExpirationMinutes(), ChronoUnit.MINUTES);
                List<String> roles = authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .map(authority -> authority.startsWith("ROLE_") ? authority.substring(5) : authority)
                        .toList();

                JwtClaimsSet claims = JwtClaimsSet.builder()
                        .issuer(jwtProperties.getIssuer())
                        .subject(authentication.getName())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim("roles", roles)
                        .build();

                // Force HS256 to match the symmetric HMAC key configured in SecurityConfig.
                JwsHeader jwsHeader = JwsHeader.with(MacAlgorithm.HS256).type("JWT").build();
                String token = jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
                user.setLocked(false);
                user.setFailedAttemps(0);  // Reset failed attempts on successful login
                userRepository.save(user);

                // Log successful login
                auditService.registerEvent(
                        "SECURITY",
                        "USER",
                        user.getId(),
                        "LOGIN_SUCCESS",
                        user.getUsername(),
                        roles.isEmpty() ? "USER" : roles.get(0),
                        "API",
                        "User logged in successfully. Roles: " + roles,
                        null
                );

                return new LoginResponse(token, "Bearer", authentication.getName(), roles, expiresAt);
            } else {
                //Si el usuario esta bloqueado lanza una excepcion de credenciales invalidas sin intentar autenticar para evitar ataques de fuerza bruta
                auditService.registerEvent(
                        "SECURITY",
                        "USER",
                        user.getId(),
                        "LOGIN_FAILED",
                        request.username(),
                        "ANONYMOUS",
                        "API",
                        "Login failed: Account is locked",
                        null
                );
                throw new InvalidCredentialsException("Account is locked");
            }
        } catch (AuthenticationException ex) {
            // Si las credenciales son incorrectas agrega intentos fallidos
            int count = user.getFailedAttemps();
            user.setFailedAttemps(count+1);

            //Si los intentos fallidos son superiores a 3 intentos bloquea el usuario
            if (user.getFailedAttemps()>=3){
                user.setLocked(true);
                auditService.registerEvent(
                        "SECURITY",
                        "USER",
                        user.getId(),
                        "LOGIN_FAILED_ACCOUNT_LOCKED",
                        request.username(),
                        "ANONYMOUS",
                        "API",
                        "Account locked after " + user.getFailedAttemps() + " failed login attempts",
                        null
                );
            } else {
                auditService.registerEvent(
                        "SECURITY",
                        "USER",
                        user.getId(),
                        "LOGIN_FAILED",
                        request.username(),
                        "ANONYMOUS",
                        "API",
                        "Login failed: Invalid credentials. Failed attempts: " + user.getFailedAttemps(),
                        null
                );
            }
            userRepository.save(user);
            throw new InvalidCredentialsException("Invalid username or password");
        }
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
            return new String[]{"User", "User"};
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length == 1) {
            return new String[]{parts[0], parts[0]};
        }
        return new String[]{parts[0], String.join(" ", java.util.Arrays.copyOfRange(parts, 1, parts.length))};
    }

    /**
     * Asigna nuevos roles a un usuario existente.
     * Reemplaza los roles actuales con los especificados en la solicitud.
     *
     * @param request Solicitud con el identificador del usuario y roles a asignar.
     * @return Mensaje confirmando la actualización de roles.
     * @throws RuntimeException Si el usuario no se encuentra en el sistema.
     */
    @Transactional
    public String assignRole(UpdateRoleRequest  request) {
        UserJpaEntity user = userRepository.findById(request.idUser()).orElseThrow(() -> new RuntimeException("User not found"));
        List<RoleJpaEntity> roles = roleRepository.findAllById(request.role());

        String oldRoles = user.getRoles().stream().map(RoleJpaEntity::getName).toList().toString();
        String newRoles = roles.stream().map(RoleJpaEntity::getName).toList().toString();

        user.setRoles(new LinkedHashSet<>(roles));
        // System.out.println("user: " + user + " roles: " + user.getRoles());
        userRepository.save(user);

        // Log role assignment
        auditService.registerEvent(
                "SECURITY",
                "USER",
                user.getId(),
                "ROLE_ASSIGNED",
                user.getUsername(),
                "ADMIN",
                "API",
                "Roles changed from " + oldRoles + " to " + newRoles,
                null
        );

        return "User" + user.getFullName() + " updated with roles "; //+ roles.stream().map(RoleJpaEntity::getName).toList();
    }
}

