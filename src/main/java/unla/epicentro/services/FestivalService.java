package unla.epicentro.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.FestivalRepository;

@Service
public class FestivalService {

	private final FestivalRepository festivalRepository;
	private final EmpleadoService empleadoService;

	public FestivalService(FestivalRepository festivalRepository, EmpleadoService empleadoService) {
		this.festivalRepository = festivalRepository;
		this.empleadoService = empleadoService;
	}

	public List<Festival> festivalesDe(UnidadDeVenta unidad) {
		return festivalRepository.findByUnidadesContainingOrderByFechaInicioAsc(unidad);
	}

	public Festival festivalDe(UnidadDeVenta unidad, Long festivalId) {
		return festivalesDe(unidad).stream()
				.filter(f -> f.getId().equals(festivalId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Tu unidad no está inscripta en ese festival"));
	}

	// Festivales de la unidad que todavía no terminaron (para asignar personal)
	public List<Festival> festivalesVigentesDe(UnidadDeVenta unidad) {
		LocalDate hoy = LocalDate.now();
		return festivalesDe(unidad).stream()
				.filter(f -> !f.getFechaFin().isBefore(hoy))
				.toList();
	}

	// Festivales que no terminaron y en los que la unidad todavía no participa
	public List<Festival> festivalesDisponibles(UnidadDeVenta unidad) {
		List<Festival> propios = festivalesDe(unidad);
		return festivalRepository.findByFechaFinGreaterThanEqualOrderByFechaInicioAsc(LocalDate.now()).stream()
				.filter(f -> !f.getUnidades().contains(unidad))
				.filter(f -> propios.stream().noneMatch(p -> seSuperponen(p, f)))
				.toList();
	}

	@Transactional
	public Festival asociar(UnidadDeVenta unidad, Long festivalId) {
		Festival festival = festivalRepository.findById(festivalId)
				.orElseThrow(() -> new IllegalArgumentException("El festival no existe"));
		if (festival.getFechaFin().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede inscribir la unidad en un festival que ya terminó");
		}
		// Una unidad no puede estar en dos festivales al mismo tiempo
		festivalesDe(unidad).stream()
				.filter(f -> !f.getId().equals(festival.getId()) && seSuperponen(f, festival))
				.findFirst()
				.ifPresent(f -> {
					throw new IllegalArgumentException("Las fechas se superponen con " + f.getNombre() + ", en el que ya participa tu unidad");
				});
		if (!festival.getUnidades().add(unidad)) {
			throw new IllegalArgumentException("La unidad ya está inscripta en " + festival.getNombre());
		}
		// El personal de la unidad pasa a trabajar también en este festival
		empleadoService.sumarPersonalAFestival(unidad, festival);
		return festival;
	}

	private boolean seSuperponen(Festival a, Festival b) {
		return !a.getFechaInicio().isAfter(b.getFechaFin()) && !b.getFechaInicio().isAfter(a.getFechaFin());
	}
}
