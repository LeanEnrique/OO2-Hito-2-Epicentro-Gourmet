package unla.epicentro.services;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import unla.epicentro.dtos.EmpleadoDTO;
import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Cocinero;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Persona;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.entities.Usuario;
import unla.epicentro.repositories.AsignacionPersonalRepository;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.repositories.PersonaRepository;
import unla.epicentro.repositories.UsuarioRepository;

@Service
public class EmpleadoService {

	private final PersonaRepository personaRepository;
	private final UsuarioRepository usuarioRepository;
	private final AsignacionPersonalRepository asignacionRepository;
	private final FestivalRepository festivalRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailService mailService;

	private final SecureRandom random = new SecureRandom();

	public EmpleadoService(
			PersonaRepository personaRepository,
			UsuarioRepository usuarioRepository,
			AsignacionPersonalRepository asignacionRepository,
			FestivalRepository festivalRepository,
			PasswordEncoder passwordEncoder,
			MailService mailService) {

		this.personaRepository = personaRepository;
		this.usuarioRepository = usuarioRepository;
		this.asignacionRepository = asignacionRepository;
		this.festivalRepository = festivalRepository;
		this.passwordEncoder = passwordEncoder;
		this.mailService = mailService;
	}

	public List<Persona> buscar(UnidadDeVenta unidad, String busqueda, boolean activo) {
		if (busqueda == null) {
			busqueda = "";
		}

		return personaRepository.buscarEmpleadosDeUnidad(
				unidad,
				busqueda.trim(),
				activo);
	}

	@Transactional
	public void crear(EmpleadoDTO dto, UnidadDeVenta unidad) {

		validarDatos(dto, null);

		if (dto.getFestivalId() == null) {
			throw new IllegalArgumentException("Debe seleccionar un festival");
		}

		Festival festival = festivalRepository.findById(dto.getFestivalId())
				.orElseThrow(() -> new IllegalArgumentException("El festival no existe"));

		if (festival.getFechaFin().isBefore(LocalDate.now())) {
			throw new IllegalArgumentException("No se puede asignar un empleado a un festival que ya terminó");
		}

		Persona empleado = crearPersona(dto);

		empleado.setFechaIngreso(LocalDate.now());
		empleado.setActivo(true);

		personaRepository.save(empleado);

		String username = dto.getEmail().trim().toLowerCase();
		String password = generarPassword();

		Usuario usuario = new Usuario();
		usuario.setUsername(username);
		usuario.setPassword(passwordEncoder.encode(password));
		usuario.setRol(unla.epicentro.entities.Rol.EMPLEADO);
		usuario.setPersona(empleado);
		usuario.setActivo(true);

		usuarioRepository.save(usuario);

		AsignacionPersonal asignacion = new AsignacionPersonal();
		asignacion.setPersona(empleado);
		asignacion.setFestival(festival);
		asignacion.setUnidad(unidad);

		asignacionRepository.save(asignacion);

		enviarDatosAcceso(empleado, username, password);
	}

	@Transactional
	public void actualizar(EmpleadoDTO dto, Long id, UnidadDeVenta unidad) {

		Persona empleado = obtenerEmpleadoDeUnidad(id, unidad);

		validarDatos(dto, id);

		empleado.setNombre(dto.getNombre().trim());
		empleado.setApellido(dto.getApellido().trim());
		empleado.setDni(dto.getDni().trim());
		empleado.setEmail(dto.getEmail().trim().toLowerCase());
		empleado.setFechaNacimiento(dto.getFechaNacimiento());

		actualizarTipo(empleado, dto);

		personaRepository.save(empleado);

		Usuario usuario = usuarioRepository.findByPersona(empleado)
				.orElseThrow(() -> new IllegalStateException("El empleado no tiene usuario asociado"));

		String nuevoUsername = empleado.getEmail().trim().toLowerCase();

		if (!usuario.getUsername().equals(nuevoUsername)) {

			if (usuarioRepository.existsByUsernameAndIdNot(
					nuevoUsername,
					usuario.getId())) {

				throw new IllegalArgumentException("Ya existe un usuario con ese email");
			}

			usuario.setUsername(nuevoUsername);
			usuarioRepository.save(usuario);
		}
	}

	@Transactional
	public void darDeBaja(Long id, UnidadDeVenta unidad) {

		Persona empleado = obtenerEmpleadoDeUnidad(id, unidad);

		empleado.setActivo(false);
		personaRepository.save(empleado);

		Usuario usuario = usuarioRepository.findByPersona(empleado)
				.orElseThrow(() -> new IllegalStateException("El empleado no tiene usuario asociado"));

		usuario.setActivo(false);
		usuarioRepository.save(usuario);
	}

	@Transactional
	public void reactivar(Long id, UnidadDeVenta unidad) {

		Persona empleado = personaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("El empleado no existe"));

		if (!asignacionRepository.existsByPersonaAndUnidad(empleado, unidad)) {
			throw new IllegalArgumentException("El empleado no pertenece a esta unidad");
		}

		empleado.setActivo(true);
		personaRepository.save(empleado);

		Usuario usuario = usuarioRepository.findByPersona(empleado)
				.orElseThrow(() -> new IllegalStateException("El empleado no tiene usuario asociado"));

		usuario.setActivo(true);
		usuarioRepository.save(usuario);
	}

	public Persona obtenerEmpleadoDeUnidad(Long id, UnidadDeVenta unidad) {

		Persona empleado = personaRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("El empleado no existe"));

		if (!asignacionRepository.existsByPersonaAndUnidad(empleado, unidad)) {
			throw new IllegalArgumentException("El empleado no pertenece a esta unidad");
		}

		if (!(empleado instanceof Cajero) && !(empleado instanceof Cocinero)) {
			throw new IllegalArgumentException("La persona indicada no es empleada");
		}

		return empleado;
	}

	private Persona crearPersona(EmpleadoDTO dto) {

		if ("CAJERO".equals(dto.getTipo())) {

			if (dto.getTurno() == null) {
				throw new IllegalArgumentException("El cajero debe tener un turno");
			}

			Cajero cajero = new Cajero();
			cajero.setTurno(dto.getTurno());

			setDatosComunes(cajero, dto);

			return cajero;
		}

		if ("COCINERO".equals(dto.getTipo())) {

			if (dto.getCategoria() == null) {
				throw new IllegalArgumentException("El cocinero debe tener una categoría");
			}

			Cocinero cocinero = new Cocinero();
			cocinero.setCategoria(dto.getCategoria());
			cocinero.setEspecialidad(dto.getEspecialidad());

			setDatosComunes(cocinero, dto);

			return cocinero;
		}

		throw new IllegalArgumentException("El tipo de empleado no es válido");
	}

	private void actualizarTipo(Persona empleado, EmpleadoDTO dto) {

		if ("CAJERO".equals(dto.getTipo())) {

			if (!(empleado instanceof Cajero)) {
				throw new IllegalArgumentException("No se puede cambiar el tipo de empleado de esta forma");
			}

			if (dto.getTurno() == null) {
				throw new IllegalArgumentException("El cajero debe tener un turno");
			}

			((Cajero) empleado).setTurno(dto.getTurno());

			return;
		}

		if ("COCINERO".equals(dto.getTipo())) {

			if (!(empleado instanceof Cocinero)) {
				throw new IllegalArgumentException("No se puede cambiar el tipo de empleado de esta forma");
			}

			if (dto.getCategoria() == null) {
				throw new IllegalArgumentException("El cocinero debe tener una categoría");
			}

			Cocinero cocinero = (Cocinero) empleado;
			cocinero.setCategoria(dto.getCategoria());
			cocinero.setEspecialidad(dto.getEspecialidad());

			return;
		}

		throw new IllegalArgumentException("El tipo de empleado no es válido");
	}

	private void setDatosComunes(Persona persona, EmpleadoDTO dto) {

		persona.setNombre(dto.getNombre().trim());
		persona.setApellido(dto.getApellido().trim());
		persona.setDni(dto.getDni().trim());
		persona.setEmail(dto.getEmail().trim().toLowerCase());
		persona.setFechaNacimiento(dto.getFechaNacimiento());

		if (!persona.esMayorDeEdad()) {
			throw new IllegalArgumentException(
					"El empleado debe ser mayor de edad");
		}
	}

	private void validarDatos(EmpleadoDTO dto, Long id) {

		if (personaRepository.existsByDniAndIdNot(dto.getDni().trim(), id)) {
			throw new IllegalArgumentException(
					"Ya existe una persona con ese DNI");
		}

		if (personaRepository.existsByEmailAndIdNot(
				dto.getEmail().trim().toLowerCase(),
				id)) {

			throw new IllegalArgumentException(
					"Ya existe una persona con ese email");
		}

		if (dto.getFechaNacimiento() == null ||
				!dto.getFechaNacimiento().isBefore(LocalDate.now())) {

			throw new IllegalArgumentException(
					"La fecha de nacimiento no es válida");
		}

		if (java.time.Period.between(
				dto.getFechaNacimiento(),
				LocalDate.now()).getYears() < Persona.MAYORIA_DE_EDAD) {

			throw new IllegalArgumentException(
					"El empleado debe ser mayor de edad");
		}
	}

	private String generarPassword() {

		String caracteres =
				"ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789";

		StringBuilder password = new StringBuilder();

		for (int i = 0; i < 10; i++) {
			password.append(
					caracteres.charAt(
							random.nextInt(caracteres.length())));
		}

		return password.toString();
	}

	private void enviarDatosAcceso(
			Persona empleado,
			String username,
			String password) {

		String asunto = "Alta de empleado - Epicentro Gourmet";

		String texto = "Hola " + empleado.getNombre() + ",\n\n" + "Se creó tu usuario para Epicentro Gourmet.\n\n" + "Usuario: " + username + "\n" + "Contraseña: " + password + "\n\n" + "Por seguridad, no compartas estos datos con otras personas.";

		mailService.enviar(
				empleado.getEmail(),
				asunto,
				texto);
	}
}