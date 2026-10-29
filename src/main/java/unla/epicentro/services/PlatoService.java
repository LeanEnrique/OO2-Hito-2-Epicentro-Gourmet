package unla.epicentro.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.dtos.PlatoDTO;
import unla.epicentro.entities.Plato;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.PlatoRepository;

@Service
public class PlatoService {

	private final PlatoRepository platoRepository;

	public PlatoService(PlatoRepository platoRepository) {
		this.platoRepository = platoRepository;
	}

	public List<Plato> buscar(UnidadDeVenta unidad, String busqueda) {

		if (busqueda == null || busqueda.isBlank()) {
			return platoRepository
					.findByUnidadAndActivoTrueOrderByNombreAsc(unidad);
		}

		return platoRepository
				.findByUnidadAndActivoTrueAndNombreContainingIgnoreCaseOrderByNombreAsc(
						unidad,
						busqueda.trim());
	}

	@Transactional
	public void crear(PlatoDTO dto, UnidadDeVenta unidad) {

		validar(dto);

		Plato plato = new Plato();
		plato.setNombre(dto.getNombre().trim());
		plato.setPrecio(dto.getPrecio());
		plato.setCosto(dto.getCosto());
		plato.setUnidad(unidad);
		plato.setActivo(true);

		platoRepository.save(plato);
	}

	@Transactional
	public void actualizar(
			Long id,
			PlatoDTO dto,
			UnidadDeVenta unidad) {

		validar(dto);

		Plato plato = obtenerDeUnidad(id, unidad);

		plato.setNombre(dto.getNombre().trim());
		plato.setPrecio(dto.getPrecio());
		plato.setCosto(dto.getCosto());

		platoRepository.save(plato);
	}

	@Transactional
	public void darDeBaja(Long id, UnidadDeVenta unidad) {

		Plato plato = obtenerDeUnidad(id, unidad);

		plato.setActivo(false);

		platoRepository.save(plato);
	}

	@Transactional
	public void reactivar(Long id, UnidadDeVenta unidad) {

		Plato plato = platoRepository.findByIdAndUnidad(id, unidad)
				.orElseThrow(() -> new IllegalArgumentException("El plato no pertenece a esta unidad"));

		plato.setActivo(true);

		platoRepository.save(plato);
	}

	public Plato obtenerDeUnidad(Long id, UnidadDeVenta unidad) {

		Plato plato = platoRepository.findByIdAndUnidad(id, unidad)
				.orElseThrow(() -> new IllegalArgumentException("El plato no pertenece a esta unidad"));

		if (!plato.isActivo()) {
			throw new IllegalArgumentException("El plato está dado de baja");
		}

		return plato;
	}

	private void validar(PlatoDTO dto) {

		if (dto.getNombre() == null ||
				dto.getNombre().isBlank()) {

			throw new IllegalArgumentException("El nombre del plato es obligatorio");
		}

		if (dto.getPrecio() == null ||
				dto.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

			throw new IllegalArgumentException("El precio debe ser mayor a cero");
		}

		if (dto.getCosto() == null ||
				dto.getCosto().compareTo(BigDecimal.ZERO) <= 0) {

			throw new IllegalArgumentException("El costo debe ser mayor a cero");
		}

		if (dto.getCosto().compareTo(dto.getPrecio()) > 0) {

			throw new IllegalArgumentException("El costo no puede ser mayor que el precio");
		}
	}
}