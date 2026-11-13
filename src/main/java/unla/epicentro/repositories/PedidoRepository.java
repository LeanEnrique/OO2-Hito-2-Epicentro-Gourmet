package unla.epicentro.repositories;

import java.util.List;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByUnidadOrderByFechaDescIdDesc(UnidadDeVenta unidad);
	List<Pedido> findByFestival(Festival festival);
	List<Pedido> findByFestivalAndUnidad(Festival festival, UnidadDeVenta unidad);
	List<Pedido> findByFestivalAndUnidadAndFechaBetween(Festival festival, UnidadDeVenta unidad, LocalDate desde, LocalDate hasta);
	List<Pedido> findByCajaOrderByIdDesc(Caja caja);

	// Historial de pedidos que cargó un cajero, en todas sus cajas
	List<Pedido> findByCajaCajeroOrderByFechaDescIdDesc(Cajero cajero);
}
