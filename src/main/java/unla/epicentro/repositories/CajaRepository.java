package unla.epicentro.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.EstadoCaja;

public interface CajaRepository extends JpaRepository<Caja, Long> {

	Optional<Caja> findFirstByCajeroAndEstadoOrderByFechaDescIdDesc(Cajero cajero, EstadoCaja estado);

	List<Caja> findByCajeroOrderByFechaDescIdDesc(Cajero cajero);

	Optional<Caja> findFirstByCajeroOrderByFechaDescIdDesc(Cajero cajero);
}
