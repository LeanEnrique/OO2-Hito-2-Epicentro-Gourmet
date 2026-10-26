package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Persona;

public interface AsignacionPersonalRepository extends JpaRepository<AsignacionPersonal, Long> {

	List<AsignacionPersonal> findByPersonaOrderByFestivalFechaInicioDesc(Persona persona);
}
