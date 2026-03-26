package cooviteCobranza.security.auth.application.service;

import cooviteCobranza.security.auth.application.dto.AuthResponse;
import cooviteCobranza.security.auth.application.dto.LoginRequest;
import cooviteCobranza.security.auth.application.dto.RegisterUserRequest;
import cooviteCobranza.security.auth.application.dto.UserResponse;
import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.model.Role;
import cooviteCobranza.security.auth.domain.repository.UserRepository;
import cooviteCobranza.security.jwt.JwtTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application Service: AuthApplicationService
 * 
 * Principios DDD:
 * 1. Orquesta la lógica de casos de uso (login, register)
 * 2. Traduce entre DTOs (entrada/salida) y modelos de dominio
 * 3. Utiliza el repositorio para persistencia
 * 4. Implementa transacciones
 * 5. Genera eventos de dominio (si aplica)
 * 
 * Patrones:
 * - Application Service
 * - DTO Mapper
 */
@Slf4j
@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    
    @Value("${security.jwt.expiration-seconds:86400}")
    private long tokenExpirationSeconds;

    public AuthApplicationService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  AuthenticationManager authenticationManager,
                                  JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    /**
     * Caso de uso: Registrar nuevo usuario
     * 
     * Flujo:
     * 1. Validar que el email no existe
     * 2. Crear modelo de dominio User
     * 3. Encriptar contraseña
     * 4. Persistir usuario
     * 5. Retornar respuesta
     */
    @Transactional
    public UserResponse register(RegisterUserRequest request) {
        log.info("Registrando nuevo usuario con email: {}", request.email());
        
        // Verificar que el email no está registrado
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Intento de registro con email ya existente: {}", request.email());
            throw new IllegalArgumentException("El email " + request.email() + " ya está registrado");
        }

        // Crear usuario de dominio
        User user = User.create(request.name(), request.lastname(), request.email(), request.password());
        
        // Encriptar contraseña (la contraseña viene sin encriptar del DTO)
        // En un caso real, la contraseña ya debería estar encriptada, pero aquí lo hacemos por claridad
        user.changePassword(passwordEncoder.encode(request.password()));
        
        // Asignar rol por defecto
        Role defaultRole = Role.create("ROLE_USER", "Usuario estándar");
        user.assignRole(defaultRole);
        
        // Persistir usuario
        User savedUser = userRepository.save(user);
        
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());
        
        // Retornar DTO de respuesta
        return UserResponse.fromDomain(savedUser);
    }

    /**
     * Caso de uso: Login/Autenticación
     * 
     * Flujo:
     * 1. Autenticar credenciales con Spring Security
     * 2. Generar JWT token
     * 3. Registrar login en el usuario
     * 4. Retornar token en formato OAuth2
     */
    @Transactional
    public AuthResponse login(LoginRequest request) {
        log.info("Intentando login para usuario: {}", request.email());
        
        try {
            // Autenticar con Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken.unauthenticated(
                            request.email(),
                            request.password()
                    )
            );

            // Obtener usuario de dominio
            User user = userRepository.findByEmail(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Registrar login en el dominio
            user.recordLogin();
            userRepository.save(user);

            // Generar JWT token
            String token = jwtTokenService.generateToken(authentication);

            log.info("Login exitoso para usuario: {}", request.email());

            // Retornar respuesta OAuth2
            return new AuthResponse(
                    token,
                    "Bearer",
                    tokenExpirationSeconds,
                    UserResponse.fromDomain(user)
            );
        } catch (AuthenticationException e) {
            log.warn("Login fallido para usuario: {} - Razón: {}", request.email(), e.getMessage());
            throw new BadCredentialsException("Email o contraseña inválidos");
        }
    }

    /**
     * Obtener información del usuario autenticado
     */
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return UserResponse.fromDomain(user);
    }

    /**
     * Cambiar contraseña de usuario
     */
    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        log.info("Cambiando contraseña para usuario: {}", email);
        
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Verificar contraseña antigua
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("La contraseña actual es incorrecta");
        }

        // Cambiar a nueva contraseña
        user.changePassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        log.info("Contraseña cambiada exitosamente para usuario: {}", email);
    }

    /**
     * Activar/Desactivar usuario (solo admin)
     */
    @Transactional
    public void toggleUserStatus(Long userId, boolean activate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        if (activate) {
            user.activate();
        } else {
            user.deactivate();
        }
        
        userRepository.save(user);
    }
}

