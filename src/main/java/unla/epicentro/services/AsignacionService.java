package unla.epicentro.services;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Persona;
import unla.epicentro.repositories.AsignacionPersonalRepository;

// Dónde trabaja (o trabajó) un empleado: festival y unidad de venta de cada asignación
@Service
public class AsignacionService {

	private final AsignacionPersonalRepository asignacionRepository;

	public AsignacionService(AsignacionPersonalRepository asignacionRepository) {
		this.asignacionRepository = asignacionRepository;
	}

	// Todas las asignaciones del empleado, del festival más reciente al más viejo
	public List<AsignacionPersonal> historial(Persona empleado) {
		return asignacionRepository.findByPersonaOrderByFestivalFechaInicioDesc(empleado);
	}

	// Festival en curso hoy en el que tiene que trabajar y en qué unidad
	public Optional<AsignacionPersonal> actual(Persona empleado) {
		LocalDate hoy = LocalDate.now();
		return historial(empleado).stream()
				.filter(a -> a.getFestival().estaActivo(hoy))
				.findFirst();
	}

	// Si hoy no trabaja, el festival que le toca más pronto
	public Optional<AsignacionPersonal> proxima(Persona empleado) {
		LocalDate hoy = LocalDate.now();
		return historial(empleado).stream()
				.filter(a -> a.getFestival().getFechaInicio().isAfter(hoy))
				.min(Comparator.comparing(a -> a.getFestival().getFechaInicio()));
	}
}
