package cooviteCobranza.security.auth.application.service;

import cooviteCobranza.security.auth.domain.model.User;
import cooviteCobranza.security.auth.domain.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service: UserDetailService
 * 
 * Implementa UserDetailsService de Spring Security
 * Carga los detalles del usuario desde la base de datos
 * Convierte modelos de dominio a objetos de seguridad de Spring
 */
@Slf4j
@Service
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository;

	public UserDetailService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Cargando usuario: {}", username);
		
		// Buscar usuario por email (usamos email como username)
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> {
					log.warn("Usuario no encontrado: {}", username);
					return new UsernameNotFoundException("Usuario no encontrado: " + username);
				});

		// Validar que el usuario está activo
		if (!user.isActive()) {
			log.warn("Usuario inactivo: {}", username);
			throw new UsernameNotFoundException("Usuario inactivo: " + username);
		}

		log.debug("Usuario cargado exitosamente: {}", username);

		// Convertir roles de dominio a autoridades de Spring
		Collection<GrantedAuthority> authorities = user.getRoles().stream()
				.map(role -> new SimpleGrantedAuthority(role.getName()))
				.collect(Collectors.toList());

		// Retornar UserDetails de Spring Security
		return org.springframework.security.core.userdetails.User
				.builder()
				.username(user.getEmail())
				.password(user.getPassword())
				.authorities(authorities)
				.disabled(!user.isActive())
				.build();
	}
}
