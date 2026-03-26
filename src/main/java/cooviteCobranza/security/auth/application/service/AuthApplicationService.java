package cooviteCobranza.security.auth.application.service;

import cooviteCobranza.security.auth.application.dto.AuthResponse;
import cooviteCobranza.security.auth.application.dto.LoginRequest;
import cooviteCobranza.security.auth.application.dto.RegisterUserRequest;
import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.repository.UserRepository;
import cooviteCobranza.security.jwt.JwtTokenService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;

    public AuthApplicationService(UserRepository userRepository,
                                  PasswordEncoder passwordEncoder,
                                  AuthenticationManager authenticationManager,
                                  JwtTokenService jwtTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public void register(RegisterUserRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }

        User user = new User(
                null,
                request.name(),
                request.lastname(),
                request.email(),
                passwordEncoder.encode(request.password()),
                "ROLE_USER"
        );

        userRepository.create(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(request.email(), request.password())
        );

        String token = jwtTokenService.generateToken(authentication);
        return new AuthResponse(token, "Bearer", jwtTokenService.getExpirationInSeconds());
    }
}

