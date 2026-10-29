package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Persona;
import unla.epicentro.entities.UnidadDeVenta;

public interface AsignacionPersonalRepository extends JpaRepository<AsignacionPersonal, Long> {

	List<AsignacionPersonal> findByPersonaOrderByFestivalFechaInicioDesc(Persona persona);

	boolean existsByPersonaAndUnidad(Persona persona, UnidadDeVenta unidad);

	boolean existsByPersonaAndUnidadAndFestival(
			Persona persona,
			UnidadDeVenta unidad,
			unla.epicentro.entities.Festival festival);
}
