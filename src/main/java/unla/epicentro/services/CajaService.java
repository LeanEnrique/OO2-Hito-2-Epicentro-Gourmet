package unla.epicentro.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.EstadoCaja;
import unla.epicentro.entities.Pedido;
import unla.epicentro.repositories.CajaRepository;
import unla.epicentro.repositories.PedidoRepository;

// CU Cerrar Caja: apertura, cierre y reanudación de la caja de un cajero
@Service
public class CajaService {

	private final CajaRepository cajaRepository;
	private final PedidoRepository pedidoRepository;

	public CajaService(CajaRepository cajaRepository, PedidoRepository pedidoRepository) {
		this.cajaRepository = cajaRepository;
		this.pedidoRepository = pedidoRepository;
	}

	public Optional<Caja> abiertaDe(Cajero cajero) {
		return cajaRepository.findFirstByCajeroAndEstadoOrderByFechaDescIdDesc(cajero, EstadoCaja.ABIERTA);
	}

	public List<Caja> historial(Cajero cajero) {
		return cajaRepository.findByCajeroOrderByFechaDescIdDesc(cajero);
	}

	public List<Pedido> pedidosDe(Caja caja) {
		return pedidoRepository.findByCajaOrderByIdDesc(caja);
	}

	// Las ventas salen de los pedidos que el cajero cargó con esta caja abierta, no se tipean a mano
	public BigDecimal ventasDelDia(Caja caja) {
		return pedidosDe(caja).stream()
				.map(Pedido::getTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	// Solo se puede reanudar la última caja del cajero, si está cerrada y es de hoy
	public Optional<Caja> reanudableDe(Cajero cajero) {
		return cajaRepository.findFirstByCajeroOrderByFechaDescIdDesc(cajero)
				.filter(c -> !c.estaAbierta() && c.getFecha().equals(LocalDate.now()));
	}

	@Transactional
	public Caja reanudar(Cajero cajero) {

		if (abiertaDe(cajero).isPresent()) {
			throw new IllegalArgumentException("Ya tenés una caja abierta");
		}

		Caja caja = reanudableDe(cajero)
				.orElseThrow(() -> new IllegalArgumentException("Solo se puede reanudar la última caja cerrada hoy"));

		if (!caja.getFestival().estaActivo(LocalDate.now())) {
			throw new IllegalArgumentException("El festival de esa caja ya no está en curso");
		}

		// Los totales se vuelven a calcular cuando se cierre de nuevo
		caja.setEstado(EstadoCaja.ABIERTA);
		caja.setFechaHoraCierre(null);

		return cajaRepository.save(caja);
	}

	@Transactional
	public Caja abrir(Cajero cajero, AsignacionPersonal asignacion) {

		if (cajero == null) {
			throw new IllegalArgumentException("El cajero es obligatorio");
		}

		if (asignacion == null || asignacion.getUnidad() == null || asignacion.getFestival() == null) {
			throw new IllegalArgumentException("Hoy no tenés un festival activo asignado");
		}

		if (abiertaDe(cajero).isPresent()) {
			throw new IllegalArgumentException("Ya tenés una caja abierta, cerrala antes de abrir otra");
		}

		Caja caja = new Caja();
		caja.setFecha(LocalDate.now());
		caja.setFestival(asignacion.getFestival());
		caja.setUnidad(asignacion.getUnidad());
		caja.setCajero(cajero);
		caja.setTurno(cajero.getTurno());
		caja.setEstado(EstadoCaja.ABIERTA);

		return cajaRepository.save(caja);
	}

	@Transactional
	public Caja cerrar(Cajero cajero) {

		Caja caja = abiertaDe(cajero)
				.orElseThrow(() -> new IllegalArgumentException("No tenés ninguna caja abierta para cerrar"));

		if (!caja.estaAbierta()) {
			throw new IllegalArgumentException("La caja ya está cerrada");
		}

		// Cierre automático: los totales salen de los pedidos de la caja
		caja.setCantidadPedidos(pedidosDe(caja).size());
		caja.setVentasDelDia(ventasDelDia(caja));
		caja.setFechaHoraCierre(LocalDateTime.now());
		caja.setEstado(EstadoCaja.CERRADA);

		return cajaRepository.save(caja);
	}
}
