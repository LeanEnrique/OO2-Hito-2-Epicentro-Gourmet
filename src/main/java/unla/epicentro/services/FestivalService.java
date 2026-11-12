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

	public FestivalService(FestivalRepository festivalRepository) {
		this.festivalRepository = festivalRepository;
	}

	public List<Festival> festivalesDe(UnidadDeVenta unidad) {
		return festivalRepository.findByUnidadesContainingOrderByFechaInicioAsc(unidad);
	}

	public Festival festivalDe(UnidadDeVenta unidad, Long festivalId) {
		Festival festival = festivalesDe(unidad).stream()
				.filter(f -> f.getId().equals(festivalId))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Tu unidad no está inscripta en ese festival"));
		validarPeriodo(festival);
		return festival;
	}

	// Validación de carga: no se opera sobre un festival cuyo período es incoherente
	public void validarPeriodo(Festival festival) {
		if (festival != null && !festival.isPeriodoValido()) {
			throw new IllegalArgumentException("El período del festival " + festival.getNombre()
					+ " no es válido: la fecha de fin no puede ser anterior a la de inicio");
		}
	}

	// Festivales que no terminaron y en los que la unidad todavía no participa
	public List<Festival> festivalesDisponibles(UnidadDeVenta unidad) {
		return festivalRepository.findByFechaFinGreaterThanEqualOrderByFechaInicioAsc(LocalDate.now()).stream()
				.filter(f -> !f.getUnidades().contains(unidad))
				.toList();
	}

	@Transactional
	public Festival asociar(UnidadDeVenta unidad, Long festivalId) {
		Festival festival = festivalRepository.findById(festivalId)
				.orElseThrow(() -> new IllegalArgumentException("El festival no existe"));
		validarPeriodo(festival);
		if (festival.getFechaFin().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede inscribir la unidad en un festival que ya terminó");
		}
		if (!festival.getUnidades().add(unidad)) {
			throw new IllegalArgumentException("La unidad ya está inscripta en " + festival.getNombre());
		}
		return festival;
	}
}
