package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;

public interface PlatoRepository extends JpaRepository<Plato, Long> {

	List<Plato> findByUnidadAndActivoTrueOrderByNombreAsc(UnidadDeVenta unidad);
}
