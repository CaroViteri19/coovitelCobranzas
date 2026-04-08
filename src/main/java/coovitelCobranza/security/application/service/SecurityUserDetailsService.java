package coovitelCobranza.security.application.service;

import coovitelCobranza.security.persistence.entity.RoleJpaEntity;
import coovitelCobranza.security.persistence.entity.UserJpaEntity;
import coovitelCobranza.security.persistence.repository.UserJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

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
     * @param username Nombre de usuario a buscar.
     * @return Detalles del usuario para Spring Security.
     * @throws UsernameNotFoundException Si el usuario no existe en el sistema.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserJpaEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(RoleJpaEntity::getName)
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());

        return User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(user.isLocked())
                .disabled(!user.isEnabled())
                .build();
    }
}

