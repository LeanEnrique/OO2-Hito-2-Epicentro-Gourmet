package unla.epicentro.services;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.repositories.PedidoRepository;
import unla.epicentro.repositories.PlatoRepository;

@Service
public class PedidoService {

	private final PedidoRepository pedidoRepository;
	private final FestivalRepository festivalRepository;
	private final PlatoRepository platoRepository;

	public PedidoService(PedidoRepository pedidoRepository, FestivalRepository festivalRepository,
			PlatoRepository platoRepository) {
		this.pedidoRepository = pedidoRepository;
		this.festivalRepository = festivalRepository;
		this.platoRepository = platoRepository;
	}

	// Festivales de la unidad que están en curso hoy (solo en esos se pueden cargar pedidos)
	public List<Festival> festivalesActivos(UnidadDeVenta unidad) {
		LocalDate hoy = LocalDate.now();
		return festivalRepository.findByUnidadesContainingOrderByFechaInicioAsc(unidad).stream()
				.filter(f -> f.estaActivo(hoy))
				.toList();
	}

	public List<Pedido> pedidosDe(UnidadDeVenta unidad) {
		return pedidoRepository.findByUnidadOrderByFechaDescIdDesc(unidad);
	}

	// cantidades: id de plato -> cantidad pedida
	@Transactional
	public Pedido crear(UnidadDeVenta unidad, Long festivalId, Map<Long, Integer> cantidades) {
		Festival festival = festivalesActivos(unidad).stream()
				.filter(f -> f.getId().equals(festivalId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("El festival elegido no está en curso para tu unidad"));

		Pedido pedido = new Pedido();
		pedido.setFecha(LocalDate.now()); // el pedido solo puede tener la fecha del día
		pedido.setFestival(festival);
		pedido.setUnidad(unidad);

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
