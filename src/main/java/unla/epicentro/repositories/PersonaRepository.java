package unla.epicentro.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Persona;

public interface PersonaRepository extends JpaRepository<Persona, Long> {

	boolean existsByDni(String dni);

	boolean existsByEmail(String email);
}
