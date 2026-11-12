package unla.epicentro.repositories;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByUnidadOrderByFechaDescIdDesc(UnidadDeVenta unidad);
	List<Pedido> findByFestival(Festival festival);
	List<Pedido> findByFestivalAndUnidad(Festival festival, UnidadDeVenta unidad);
	List<Pedido> findByFestivalAndUnidadAndFechaBetween(Festival festival, UnidadDeVenta unidad, LocalDate desde, LocalDate hasta);
	List<Pedido> findByUnidadAndFestivalAndFecha(UnidadDeVenta unidad, Festival festival, LocalDate fecha);
}
