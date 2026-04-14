package coovitelCobranza.security.infrastructure.web;

import coovitelCobranza.security.application.dto.UserResponse;
import coovitelCobranza.security.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
