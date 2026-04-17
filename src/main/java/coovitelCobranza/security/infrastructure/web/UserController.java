package coovitelCobranza.security.infrastructure.web;

import coovitelCobranza.security.application.dto.UpdateUserRequest;
import coovitelCobranza.security.application.dto.UserResponse;
import coovitelCobranza.security.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/all")
    public ResponseEntity<List<UserResponse>>  getUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PatchMapping("/update")
    public ResponseEntity<UserResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(updateUserRequest));
    }
}
