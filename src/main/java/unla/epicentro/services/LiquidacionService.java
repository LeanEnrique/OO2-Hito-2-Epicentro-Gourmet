package unla.epicentro.services;

import java.util.List;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.LiquidacionDTO;
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
						a.getFestival().getNombre(),
						a.getUnidad().getNombreComercial(),
						empleado.getRolLaboral(),
						a.getFestival().getCantidadDias(),
						empleado.calcularSueldo(a.getFestival())))
				.toList();
	}
}
