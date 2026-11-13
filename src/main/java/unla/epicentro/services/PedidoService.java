package unla.epicentro.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.PedidoRepository;
import unla.epicentro.repositories.PlatoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final PlatoRepository platoRepository;

	public PedidoService(PedidoRepository pedidoRepository, PlatoRepository platoRepository) {
		this.pedidoRepository = pedidoRepository;
		this.platoRepository = platoRepository;
	}

	public List<Pedido> pedidosDe(UnidadDeVenta unidad) {
		return pedidoRepository.findByUnidadOrderByFechaDescIdDesc(unidad);
	}

	public List<Pedido> pedidosDe(UnidadDeVenta unidad, Festival festival) {
		return pedidosDe(unidad).stream()
				.filter(p -> p.getFestival().getId().equals(festival.getId()))
				.toList();
	}

	public List<Pedido> pedidosDe(Cajero cajero) {
		return pedidoRepository.findByCajaCajeroOrderByFechaDescIdDesc(cajero);
	}

	public List<Plato> platosDisponibles(UnidadDeVenta unidad) {
		return platoRepository.findByUnidadAndActivoTrueOrderByNombreAsc(unidad);
	}

	// El cajero carga el pedido con su caja abierta; cantidades: id de plato -> cantidad pedida
	@Transactional
	public Pedido crear(Caja caja, Map<Long, Integer> cantidades) {
		LocalDate hoy = LocalDate.now();

		if (caja == null || !caja.estaAbierta()) {
			throw new IllegalArgumentException("Abrí la caja antes de cargar pedidos");
		}
		if (!caja.getFecha().equals(hoy)) {
			throw new IllegalArgumentException("Tu caja abierta es de otro día: cerrala y abrí una nueva para cargar pedidos");
		}

		Festival festival = caja.getFestival();
		UnidadDeVenta unidad = caja.getUnidad();
		if (!festival.estaActivo(hoy)) {
			throw new IllegalArgumentException("El festival " + festival.getNombre() + " no está en curso");
		}

		Pedido pedido = new Pedido();
		pedido.setFecha(hoy); // el pedido solo puede tener la fecha del día
		pedido.setFestival(festival);
		pedido.setUnidad(unidad);
		pedido.setCaja(caja);

		for (Plato plato : platoRepository.findByUnidadAndActivoTrueOrderByNombreAsc(unidad)) {
			Integer cantidad = cantidades.get(plato.getId());
			if (cantidad == null || cantidad == 0) {
				continue;
			}
			if (cantidad < 0) {
				throw new IllegalArgumentException("La cantidad de " + plato.getNombre() + " no puede ser negativa");
			}
			pedido.agregarItem(plato, cantidad);
		}
		if (pedido.getItems().isEmpty()) {
			throw new IllegalArgumentException("El pedido debe tener al menos un plato");
		}
		return pedidoRepository.save(pedido);
	}
}
