package unla.epicentro.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

	List<Plato> findByUnidadAndActivoTrueOrderByNombreAsc(UnidadDeVenta unidad);

	List<Plato> findByUnidadAndActivoFalseOrderByNombreAsc(UnidadDeVenta unidad);

	List<Plato> findByUnidadAndActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(
			UnidadDeVenta unidad,
			String nombre);

	Optional<Plato> findByIdAndUnidad(Long id, UnidadDeVenta unidad);

	// Para no repetir nombres entre los platos activos de una unidad
	boolean existsByUnidadAndActivoTrueAndNombreIgnoreCase(UnidadDeVenta unidad, String nombre);

	boolean existsByUnidadAndActivoTrueAndNombreIgnoreCaseAndIdNot(UnidadDeVenta unidad, String nombre, Long id);
}
