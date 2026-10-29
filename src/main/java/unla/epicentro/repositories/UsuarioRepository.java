package unla.epicentro.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Persona;
import unla.epicentro.entities.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByUsername(String username);

	Optional<Usuario> findByPersona(Persona persona);

	boolean existsByUsername(String username);

	boolean existsByUsernameAndIdNot(String username, Long id);
}