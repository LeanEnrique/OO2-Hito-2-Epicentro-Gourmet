package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;

public interface FestivalRepository extends JpaRepository<Festival, Long> {

	List<Festival> findAllByOrderByFechaInicioAsc();

	List<Festival> findByUnidadesContainingOrderByFechaInicioAsc(UnidadDeVenta unidad);
}
