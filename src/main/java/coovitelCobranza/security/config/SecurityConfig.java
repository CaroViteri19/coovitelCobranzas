package coovitelCobranza.security.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import coovitelCobranza.security.application.service.SecurityUserDetailsService;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

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
                corsConfiguration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
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
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
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
     * Obtiene el gestor de autenticación de Spring Security.
     *
     * @param authenticationConfiguration Configuración de autenticación.
     * @return Gestor de autenticación.
     * @throws Exception Si ocurre un error al obtener el gestor.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
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

