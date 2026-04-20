package coovitelCobranza.security.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import coovitelCobranza.security.application.service.SecurityUserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Configuración de seguridad de Spring Security.
 * Define los filtros de seguridad, codificador de contraseñas y configuración de JWT.
 */
@Configuration
@EnableMethodSecurity
@EnableConfigurationProperties({JwtProperties.class, SecurityBootstrapProperties.class})
public class SecurityConfig {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define los puntos finales públicos y requiere autenticación para otros.
     *
     * @param http Constructor HTTP Security.
     * @param authenticationProvider Proveedor de autenticación personalizado.
     * @return Cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationProvider authenticationProvider) throws Exception {
        http
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(java.util.List.of("http://localhost:4200")); // URL de tu Angular
                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(java.util.List.of("*"));
                corsConfiguration.setAllowCredentials(true);
                return corsConfiguration;
            }))
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // H2 console usa iframes
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .authorizeHttpRequests(auth -> auth
                        // Endpoints públicos — no requieren token
                        .requestMatchers(
                                "/api/v1/auth/login",
                                "/api/v1/auth/register",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/h2-console/**",   // consola H2 solo dev
                                "/error"
                        ).permitAll()
                        // Cualquier otra petición requiere JWT válido.
                        // El rol específico se controla con @PreAuthorize en cada método.
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        return http.build();
    }

    /**
     * Convierte el JWT en un Authentication leyendo el claim "roles".
     * El backend almacena los roles sin prefijo ROLE_ (ej: "ADMINISTRADOR"),
     * por lo que aquí se añade el prefijo que espera hasRole() de Spring Security.
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            List<String> roles = jwt.getClaimAsStringList("roles");
            if (roles == null || roles.isEmpty()) {
                return Collections.emptyList();
            }
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }

    /**
     * Configura el proveedor de autenticación DAO.
     * Utiliza el servicio de detalles de usuario y el codificador de contraseñas.
     *
     * @param userDetailsService Servicio para cargar detalles del usuario.
     * @param passwordEncoder Codificador de contraseñas.
     * @return Proveedor de autenticación configurado.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(SecurityUserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /**
     * Expone el {@link AuthenticationManager} como bean reutilizable por
     * {@code AuthApplicationService} (inyectado por constructor).
     *
     * <p>Se construye directamente con {@link ProviderManager} envolviendo el
     * {@link AuthenticationProvider} ya expuesto arriba. Esta forma es la
     * recomendada en Spring Security 6 / Spring Boot 3 y evita la dependencia
     * circular que aparece al derivar el manager desde
     * {@code AuthenticationConfiguration.getAuthenticationManager()} cuando en
     * la misma clase coexisten {@code SecurityFilterChain} y
     * {@code AuthenticationProvider} (ciclo que Spring reporta de forma
     * confusa como "bean of type AuthenticationManager could not be found").
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider) {
        return new ProviderManager(authenticationProvider);
    }

    /**
     * Configura el codificador de contraseñas BCrypt.
     *
     * @return Codificador de contraseñas BCrypt.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el codificador JWT con la clave secreta.
     *
     * @param jwtProperties Propiedades de configuración JWT.
     * @return Codificador JWT configurado.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public JwtEncoder jwtEncoder(JwtProperties jwtProperties) throws Exception {
        try {
            SecretKey key = jwtSecretKey(jwtProperties);
            return new NimbusJwtEncoder(new ImmutableSecret<>(key));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create JWT encoder", e);
        }
    }

    /**
     * Configura el decodificador JWT con la clave secreta.
     *
     * @param jwtProperties Propiedades de configuración JWT.
     * @return Decodificador JWT configurado.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public JwtDecoder jwtDecoder(JwtProperties jwtProperties) throws Exception {
        SecretKey key = jwtSecretKey(jwtProperties);
        return NimbusJwtDecoder.withSecretKey(key).build();
    }

    /**
     * Crea la clave secreta para firmar y verificar JWT.
     * Asegura que la clave tenga exactamente 256 bits (32 bytes) para HMAC-SHA256.
     *
     * @param jwtProperties Propiedades con la clave secreta configurada.
     * @return Clave secreta de 256 bits para JWT.
     * @throws IllegalArgumentException Si la clave es menor a 32 caracteres.
     */
    private SecretKey jwtSecretKey(JwtProperties jwtProperties) {
        String secret = jwtProperties.getSecret();
        if (secret == null || secret.length() < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 characters long");
        }
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        // Ensure exactly 256 bits (32 bytes)
        byte[] finalKey = new byte[32];
        System.arraycopy(keyBytes, 0, finalKey, 0, Math.min(keyBytes.length, 32));
        return new SecretKeySpec(finalKey, "HmacSHA256");
    }
}

