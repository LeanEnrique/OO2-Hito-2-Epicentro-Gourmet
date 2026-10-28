package unla.epicentro.services;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.DetalleLiquidacionDTO;
import unla.epicentro.dtos.LiquidacionDTO;
import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Persona;
import unla.epicentro.repositories.AsignacionPersonalRepository;

@Service
public class LiquidacionService {

	private final AsignacionPersonalRepository asignacionRepository;

	public LiquidacionService(AsignacionPersonalRepository asignacionRepository) {
		this.asignacionRepository = asignacionRepository;
	}

	// Una línea por cada festival en el que trabajó el empleado
	public List<LiquidacionDTO> liquidar(Persona empleado) {
		return asignacionRepository.findByPersonaOrderByFestivalFechaInicioDesc(empleado).stream()
				.map(a -> new LiquidacionDTO(
						a.getFestival().getId(),
						a.getFestival().getNombre(),
						a.getUnidad().getNombreComercial(),
						empleado.getRolLaboral(),
						a.getFestival().getCantidadDias(),
						empleado.calcularSueldo(a.getFestival())))
				.toList();
	}

	// Solo se puede ver el detalle de un festival en el que el empleado trabajó; sin festivalId, el más reciente
	public Optional<DetalleLiquidacionDTO> detalle(Persona empleado, Long festivalId) {
		return asignacionRepository.findByPersonaOrderByFestivalFechaInicioDesc(empleado).stream()
				.filter(a -> festivalId == null || a.getFestival().getId().equals(festivalId))
				.findFirst()
				.map(a -> detalle(empleado, a));
	}

	private DetalleLiquidacionDTO detalle(Persona empleado, AsignacionPersonal asignacion) {
		Festival festival = asignacion.getFestival();
		BigDecimal sueldoBase = festival.getCostos().getSueldoBase();
		BigDecimal adicional = empleado.calcularAdicional(festival);
		return new DetalleLiquidacionDTO(
				festival.getNombre(),
				asignacion.getUnidad().getNombreComercial(),
				empleado.getRolLaboral(),
				festival.getCantidadDias(),
				sueldoBase,
				empleado.getConceptoAdicional(festival),
				adicional,
				sueldoBase.add(adicional),
				festival.getPorcentajeFactor(),
				festival.getDescripcionFactor(),
				empleado.calcularSueldo(festival));
	}
}
