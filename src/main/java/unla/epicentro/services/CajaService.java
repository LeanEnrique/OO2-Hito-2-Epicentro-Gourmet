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

// CU Cerrar Caja: apertura y cierre del turno de un cajero
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

	// Las ventas del día salen de los pedidos cargados por el responsable, no se tipean a mano
	public BigDecimal ventasDelDia(Caja caja) {
		return pedidoRepository
				.findByUnidadAndFestivalAndFecha(caja.getUnidad(), caja.getFestival(), caja.getFecha())
				.stream()
				.map(Pedido::getTotal)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	@Transactional
	public Caja abrir(Cajero cajero, AsignacionPersonal asignacion, BigDecimal montoInicial) {

		if (cajero == null) {
			throw new IllegalArgumentException("El cajero es obligatorio");
		}

		if (asignacion == null || asignacion.getUnidad() == null || asignacion.getFestival() == null) {
			throw new IllegalArgumentException("Hoy no tenés un festival activo asignado");
		}

		if (abiertaDe(cajero).isPresent()) {
			throw new IllegalArgumentException("Ya tenés una caja abierta, cerrala antes de abrir otra");
		}

		if (montoInicial == null || montoInicial.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("El monto inicial no puede ser negativo");
		}

		Caja caja = new Caja();
		caja.setFecha(LocalDate.now());
		caja.setFestival(asignacion.getFestival());
		caja.setUnidad(asignacion.getUnidad());
		caja.setCajero(cajero);
		caja.setTurno(cajero.getTurno());
		caja.setMontoInicial(montoInicial);
		caja.setEstado(EstadoCaja.ABIERTA);

		return cajaRepository.save(caja);
	}

	@Transactional
	public Caja cerrar(Cajero cajero, BigDecimal montoDeclarado, String observaciones) {

		Caja caja = abiertaDe(cajero)
				.orElseThrow(() -> new IllegalArgumentException("No tenés ninguna caja abierta para cerrar"));

		if (!caja.estaAbierta()) {
			throw new IllegalArgumentException("La caja ya está cerrada");
		}

		if (montoDeclarado == null || montoDeclarado.compareTo(BigDecimal.ZERO) < 0) {
			throw new IllegalArgumentException("El monto declarado no puede ser negativo");
		}

		caja.setVentasDelDia(ventasDelDia(caja));
		caja.setMontoDeclarado(montoDeclarado);
		caja.setDiferencia(montoDeclarado.subtract(caja.getMontoEsperado()));
		caja.setObservaciones(observaciones == null ? null : observaciones.trim());
		caja.setFechaHoraCierre(LocalDateTime.now());
		caja.setEstado(EstadoCaja.CERRADA);

		return cajaRepository.save(caja);
	}
}
