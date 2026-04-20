package coovitelCobranza.security.infrastructure.web;

import coovitelCobranza.security.application.dto.*;
import coovitelCobranza.security.application.service.AuthApplicationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para operaciones de autenticación y registro de usuarios.
 * Expone los puntos finales para login, registro y asignación de roles.
 */

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param authApplicationService Servicio de autenticación.
     */
    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    /**
     * Autentica un usuario y retorna un token JWT.
     *
     * @param request Solicitud con credenciales de usuario.
     * @return Respuesta con token JWT y datos del usuario.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        System.out.println(request.toString());
        return ResponseEntity.status(HttpStatus.OK).body(authApplicationService.login(request));
    }

    /**
     * Registra un nuevo usuario en el sistema.
     *
     * @param request Solicitud con datos del usuario a registrar.
     * @return Respuesta con los datos del usuario creado (código 201).
     */
    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authApplicationService.register(request));
    }

    /**
     * Asigna nuevos roles a un usuario existente.
     * Solo accesible por usuarios con rol ADMINISTRADOR.
     *
     * @param request Solicitud con el ID del usuario y los roles a asignar.
     * @return Respuesta con mensaje de confirmación.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping("/role")
    public ResponseEntity<String> assignRole(@Valid @RequestBody UpdateRoleRequest request) {
        return ResponseEntity.ok(authApplicationService.assignRole(request));
    }

    /**
     * Devuelve la lista de roles disponibles en el sistema.
     * Usado por el frontend para poblar el selector de rol al crear usuarios.
     * Solo accesible por usuarios con rol ADMINISTRADOR.
     *
     * @return Lista de roles con ID, nombre y descripción.
     */
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @GetMapping("/roles")
    public ResponseEntity<List<RoleResponse>> getRoles() {
        return ResponseEntity.ok(authApplicationService.getRoles());
    }

}

