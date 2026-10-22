package unla.epicentro.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.UnidadDeVenta;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

	List<Pedido> findByUnidadOrderByFechaDescIdDesc(UnidadDeVenta unidad);
}
