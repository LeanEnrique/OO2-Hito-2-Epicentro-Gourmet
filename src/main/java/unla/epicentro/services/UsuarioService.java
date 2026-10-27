package unla.epicentro.services;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import unla.epicentro.entities.Persona;
import unla.epicentro.entities.Usuario;
import unla.epicentro.repositories.UsuarioRepository;

@Service
public class UsuarioService implements UserDetailsService {

	private final UsuarioRepository usuarioRepository;

	public UsuarioService(UsuarioRepository usuarioRepository) {
		this.usuarioRepository = usuarioRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Usuario usuario = usuarioRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
		return User.withUsername(usuario.getUsername())
				.password(usuario.getPassword())
				.roles(usuario.getRol().name())
				.disabled(!usuario.isActivo())
				.build();
	}

	public Persona getPersona(String username) {
		return usuarioRepository.findByUsername(username)
				.map(Usuario::getPersona)
				.orElseThrow(() -> new IllegalStateException("El usuario no tiene persona asociada"));
	}
}
