package coovitelCobranza.security.infrastructure.web;

import coovitelCobranza.security.application.dto.UpdateUserRequest;
import coovitelCobranza.security.application.dto.UserResponse;
import coovitelCobranza.security.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import coovitelCobranza.security.application.dto.UpdateUserStatusRequest;
import coovitelCobranza.security.application.dto.UserSummaryResponse;
import coovitelCobranza.security.application.service.UserManagementService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoints de administracion de usuarios consumidos por Settings en frontend.
 */
@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    private final UserManagementService userManagementService;

    public UserController(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>>  getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PatchMapping("/status")
    public ResponseEntity<Void> updateStatus(@Valid @RequestBody UpdateUserStatusRequest request) {
        userManagementService.updateUserStatus(request);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMINISTRATOR')")
    @PatchMapping("/update")
    public ResponseEntity<Void> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        userManagementService.updateUser(request);
        return ResponseEntity.noContent().build();
    }
}

