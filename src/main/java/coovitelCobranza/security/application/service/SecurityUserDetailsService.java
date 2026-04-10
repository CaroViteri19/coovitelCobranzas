package coovitelCobranza.security.application.service;

import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio de detalles de usuario para Spring Security.
 * Carga los datos del usuario y sus autoridades desde la base de datos.
 */
@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserJpaRepository userRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param userRepository Repositorio de usuarios.
     */
    public SecurityUserDetailsService(UserJpaRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Carga los detalles del usuario por nombre de usuario.
     * Obtiene el usuario y sus roles para autenticación.
     *
     * @param email Nombre de usuario a buscar.
     * @return Detalles del usuario para Spring Security.
     * @throws UsernameNotFoundException Si el usuario no existe en el sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        String normalizedEmail = email == null ? "" : email.trim().toLowerCase(java.util.Locale.ROOT);
        UserJpaEntity user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        RoleJpaEntity role = user.getRoles();
        String authorityName = role == null || role.getName() == null || role.getName().isBlank()
                ? "ROLE_USER"
                : (role.getName().startsWith("ROLE_") ? role.getName() : "ROLE_" + role.getName());

        return User.builder()
                .username(user.getUsername())
                //.username(user.getEmail())
                .password(user.getPassword())
                .authorities(Collections.singleton(new SimpleGrantedAuthority(authorityName)))
                .accountLocked(user.isLocked())
                .disabled(!user.isEnabled())
                .build();
    }
}

