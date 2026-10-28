package unla.epicentro.services;

import java.time.LocalDate;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.dtos.RegistroUnidadDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.FoodTruck;
import unla.epicentro.entities.Persona;
import unla.epicentro.entities.PuestoDesarmable;
import unla.epicentro.entities.Rol;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.entities.Usuario;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.repositories.PersonaRepository;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.repositories.UsuarioRepository;

@Service
public class RegistroService {

	private final PersonaRepository personaRepository;
	private final UsuarioRepository usuarioRepository;
	private final UnidadDeVentaRepository unidadRepository;
	private final FestivalRepository festivalRepository;
	private final PasswordEncoder passwordEncoder;

	public RegistroService(PersonaRepository personaRepository, UsuarioRepository usuarioRepository,
			UnidadDeVentaRepository unidadRepository, FestivalRepository festivalRepository,
			PasswordEncoder passwordEncoder) {
		this.personaRepository = personaRepository;
		this.usuarioRepository = usuarioRepository;
		this.unidadRepository = unidadRepository;
		this.festivalRepository = festivalRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Transactional
	public UnidadDeVenta registrar(RegistroUnidadDTO dto) {
		if (usuarioRepository.existsByUsername(dto.getUsername())) {
			throw new IllegalArgumentException("El usuario '" + dto.getUsername() + "' ya existe");
		}
		if (personaRepository.existsByDni(dto.getDni())) {
			throw new IllegalArgumentException("Ya hay una persona registrada con ese DNI");
		}
		if (personaRepository.existsByEmail(dto.getEmail())) {
			throw new IllegalArgumentException("Ya hay una persona registrada con ese email");
		}
		if (unidadRepository.existsByCodigo(dto.getCodigo())) {
			throw new IllegalArgumentException("Ya existe una unidad con el código " + dto.getCodigo());
		}
		Festival festival = festivalRepository.findById(dto.getFestivalId())
				.orElseThrow(() -> new IllegalArgumentException("El festival no existe"));
		if (festival.getFechaFin().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede registrar una unidad en un festival que ya terminó");
		}

		Persona responsable = new Persona();
		responsable.setNombre(dto.getNombre());
		responsable.setApellido(dto.getApellido());
		responsable.setDni(dto.getDni());
		responsable.setEmail(dto.getEmail());
		responsable.setFechaNacimiento(dto.getFechaNacimiento());
		responsable.setFechaIngreso(LocalDate.now());
		if (!responsable.esMayorDeEdad()) {
			throw new IllegalArgumentException("El responsable debe ser mayor de edad");
		}

		UnidadDeVenta unidad = crearUnidad(dto);
		unidad.setNombreComercial(dto.getNombreComercial());
		unidad.setCodigo(dto.getCodigo().toUpperCase());
		unidad.setSuperficie(dto.getSuperficie());
		unidad.setResponsable(responsable);
		if (!unidad.tieneCodigoValido()) {
			throw new IllegalArgumentException("Código inválido: un " + unidad.getTipo()
					+ " debe tener el formato " + ("FOOD_TRUCK".equals(dto.getTipo()) ? "FT" : "PD") + " + 8 dígitos");
		}

		Usuario usuario = new Usuario();
		usuario.setUsername(dto.getUsername());
		usuario.setPassword(passwordEncoder.encode(dto.getPassword()));
		usuario.setRol(Rol.RESPONSABLE);
		usuario.setPersona(responsable);

		personaRepository.save(responsable);
		unidadRepository.save(unidad);
		usuarioRepository.save(usuario);
		festival.getUnidades().add(unidad);
		return unidad;
	}

	private UnidadDeVenta crearUnidad(RegistroUnidadDTO dto) {
		if ("FOOD_TRUCK".equals(dto.getTipo())) {
			if (dto.getPatente() == null || dto.getPatente().isBlank()) {
				throw new IllegalArgumentException("El Food Truck debe tener patente");
			}
			FoodTruck foodTruck = new FoodTruck();
			foodTruck.setPatente(dto.getPatente().toUpperCase());
			foodTruck.setConexionElectrica(dto.isConexionElectrica());
			return foodTruck;
		}
		if ("PUESTO".equals(dto.getTipo())) {
			if (dto.getCantCarpas() == null || dto.getCantCarpas() < 1 || dto.getTiempoDeArmado() == null
					|| dto.getTiempoDeArmado() < 0) {
				throw new IllegalArgumentException("El puesto debe tener al menos 1 carpa y un tiempo de armado válido");
			}
			PuestoDesarmable puesto = new PuestoDesarmable();
			puesto.setCantCarpas(dto.getCantCarpas());
			puesto.setTiempoDeArmado(dto.getTiempoDeArmado());
			return puesto;
		}
		throw new IllegalArgumentException("Tipo de unidad inválido");
	}
}
