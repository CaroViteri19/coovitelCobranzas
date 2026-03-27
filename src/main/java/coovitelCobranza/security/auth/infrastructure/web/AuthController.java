package coovitelCobranza.security.auth.infrastructure.web;

import coovitelCobranza.security.auth.application.dto.AuthResponse;
import coovitelCobranza.security.auth.application.dto.LoginRequest;
import coovitelCobranza.security.auth.application.dto.RegisterUserRequest;
import coovitelCobranza.security.auth.application.dto.UserResponse;
import coovitelCobranza.security.auth.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller: AuthController
 * 
 * Capa de presentación para autenticación OAuth2/JWT
 * 
 * Endpoints:
 * - POST /api/auth/register - Registrar nuevo usuario
 * - POST /api/auth/login - Login y obtener JWT
 * - GET /api/auth/me - Información del usuario autenticado
 * - PUT /api/auth/change-password - Cambiar contraseña
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * POST /api/auth/register
     * Registrar nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        log.info("POST /api/auth/register - Email: {}", request.email());
        UserResponse response = authApplicationService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     * Login y obtener JWT token
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("POST /api/auth/login - Email: {}", request.email());
        AuthResponse response = authApplicationService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/auth/me
     * Obtener información del usuario autenticado
     * Requiere: Authorization: Bearer <token>
     */
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getCurrentUser(Authentication authentication) {
        log.info("GET /api/auth/me - User: {}", authentication.getName());
        UserResponse response = authApplicationService.getCurrentUser(authentication.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * PUT /api/auth/change-password
     * Cambiar contraseña del usuario autenticado
     * Requiere: Authorization: Bearer <token>
     */
    @PutMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            Authentication authentication) {
        log.info("PUT /api/auth/change-password - User: {}", authentication.getName());
        authApplicationService.changePassword(
                authentication.getName(),
                request.oldPassword(),
                request.newPassword()
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * DTO: ChangePasswordRequest
     */
    public record ChangePasswordRequest(
            @NotBlank(message = "La contraseña actual es requerida")
            String oldPassword,
            
            @NotBlank(message = "La nueva contraseña es requerida")
            @Size(min = 8, max = 100, message = "La contraseña debe tener entre 8 y 100 caracteres")
            String newPassword
    ) {
    }
}

