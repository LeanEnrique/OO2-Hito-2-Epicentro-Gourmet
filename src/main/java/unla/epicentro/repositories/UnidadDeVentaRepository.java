package unla.epicentro.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Persona;
import unla.epicentro.entities.UnidadDeVenta;

public interface UnidadDeVentaRepository extends JpaRepository<UnidadDeVenta, Long> {

	boolean existsByCodigo(String codigo);

	Optional<UnidadDeVenta> findByResponsable(Persona responsable);
}
