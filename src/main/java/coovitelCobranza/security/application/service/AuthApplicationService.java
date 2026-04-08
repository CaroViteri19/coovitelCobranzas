package coovitelCobranza.security.application.service;

import coovitelCobranza.security.application.dto.LoginRequest;
import coovitelCobranza.security.application.dto.LoginResponse;
import coovitelCobranza.security.application.dto.RegisterUserRequest;
import coovitelCobranza.security.application.dto.RegisterUserResponse;
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

@Service
public class AuthApplicationService {

    private final AuthenticationManager authenticationManager;
    private final JwtEncoder jwtEncoder;
    private final JwtProperties jwtProperties;
    private final UserJpaRepository userRepository;
    private final RoleJpaRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthApplicationService(AuthenticationManager authenticationManager,
                                  JwtEncoder jwtEncoder,
                                  JwtProperties jwtProperties,
                                  UserJpaRepository userRepository,
                                  RoleJpaRepository roleRepository,
                                  PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtEncoder = jwtEncoder;
        this.jwtProperties = jwtProperties;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public RegisterUserResponse register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistsException("Username already exists");
        }

        String normalizedEmail = request.email().trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(normalizedEmail)) {
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
        user.setPassword(passwordEncoder.encode(request.password()));
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

        return new RegisterUserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                roles,
                savedUser.isEnabled()
        );
    }

    public LoginResponse login(LoginRequest request) {
        try {
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

            // Let the encoder pick the compatible key/algorithm from configured JWT material.
            String token = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
            return new LoginResponse(token, "Bearer", authentication.getName(), roles, expiresAt);
        } catch (AuthenticationException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
    }

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
}

