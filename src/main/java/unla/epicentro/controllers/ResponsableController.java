package unla.epicentro.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.ModelAttribute;

import unla.epicentro.dtos.EmpleadoDTO;
import unla.epicentro.dtos.PlatoDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Categoria;
import unla.epicentro.entities.Cocinero;
import unla.epicentro.entities.Persona;
import unla.epicentro.entities.Turno;
import unla.epicentro.repositories.PlatoRepository;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.services.PedidoService;
import unla.epicentro.services.UsuarioService;
import unla.epicentro.services.EmpleadoService;
import unla.epicentro.services.FestivalService;
import unla.epicentro.services.PlatoService;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

	private final UsuarioService usuarioService;
	private final UnidadDeVentaRepository unidadRepository;
	private final PlatoRepository platoRepository;
	private final PedidoService pedidoService;
	private final EmpleadoService empleadoService;
	private final PlatoService platoService;
	private final FestivalService festivalService;

	public ResponsableController(UsuarioService usuarioService, UnidadDeVentaRepository unidadRepository, PlatoRepository platoRepository, PedidoService pedidoService, EmpleadoService empleadoService, PlatoService platoService, FestivalService festivalService) {

		this.usuarioService = usuarioService;
		this.unidadRepository = unidadRepository;
		this.platoRepository = platoRepository;
		this.pedidoService = pedidoService;
		this.empleadoService = empleadoService;
		this.platoService = platoService;
		this.festivalService = festivalService;
	}

	@GetMapping
	public String inicio(Principal principal, Model model) {
		UnidadDeVenta unidad = unidadDe(principal);
		model.addAttribute("unidad", unidad);
		model.addAttribute("platos", platoRepository.findByUnidadAndActivoTrueOrderByNombreAsc(unidad));
		model.addAttribute("misFestivales", festivalService.festivalesDe(unidad));
		model.addAttribute("festivalesDisponibles", festivalService.festivalesDisponibles(unidad));
		return "responsable/inicio";
	}

	// Detalle del festival para la unidad: sus datos y los pedidos que tomaron los cajeros
	@GetMapping("/festivales/{id}")
	public String detalleFestival(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirect) {
		UnidadDeVenta unidad = unidadDe(principal);
		try {
			Festival festival = festivalService.festivalDe(unidad, id);
			List<Pedido> pedidos = pedidoService.pedidosDe(unidad, festival);
			model.addAttribute("festival", festival);
			model.addAttribute("pedidos", pedidos);
			model.addAttribute("recaudacion", pedidos.stream().map(Pedido::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
			return "responsable/festival";
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/responsable";
		}
	}

	@PostMapping("/festivales")
	public String inscribirseAFestival(Principal principal, @RequestParam Long festivalId, RedirectAttributes redirect) {
		try {
			var festival = festivalService.asociar(unidadDe(principal), festivalId);
			redirect.addFlashAttribute("mensaje", "Tu unidad quedó inscripta en " + festival.getNombre());
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/responsable";
	}

@GetMapping("/empleados")
	public String empleados(
			Principal principal,
			@RequestParam(required = false, defaultValue = "") String busqueda,
			@RequestParam(required = false) String tipo,
			@RequestParam(required = false) Turno turno,
			@RequestParam(required = false) Categoria categoria,
			Model model) {

		UnidadDeVenta unidad = unidadDe(principal);

		model.addAttribute("unidad", unidad);
		model.addAttribute(
				"empleados",
				empleadoService.buscar(unidad, busqueda, true, tipo, turno, categoria));
		model.addAttribute(
				"bajas",
				empleadoService.buscar(unidad, "", false));
		model.addAttribute("busqueda", busqueda);
		model.addAttribute("tipo", tipo);
		model.addAttribute("turno", turno);
		model.addAttribute("categoria", categoria);
		model.addAttribute("turnos", Turno.values());
		model.addAttribute("categorias", Categoria.values());
		model.addAttribute("tipos", List.of("CAJERO", "COCINERO"));
		model.addAttribute("festivalesPorEmpleado", empleadoService.festivalesPorEmpleado(unidad));

		return "responsable/empleados";
	}

	@GetMapping("/empleados/nuevo")
	public String nuevoEmpleado(Principal principal, Model model) {

		model.addAttribute("empleadoDTO", new EmpleadoDTO());
		model.addAttribute("festivales", festivalService.festivalesVigentesDe(unidadDe(principal)));

		return "responsable/form-empleado";
	}

	@PostMapping("/empleados")
	public String crearEmpleado(Principal principal, @Valid @ModelAttribute("empleadoDTO") EmpleadoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		UnidadDeVenta unidad = unidadDe(principal);

		if (result.hasErrors()) {

			model.addAttribute("festivales", festivalService.festivalesVigentesDe(unidad));
			return "responsable/form-empleado";
		}

		try {
			String passwordTemporal = empleadoService.crear(dto, unidad);

			if (passwordTemporal == null) {
				redirect.addFlashAttribute("mensaje", "Empleado creado correctamente. Se enviaron sus datos de acceso por email.");
			} else {
				// Sin SMTP configurado el mail no sale: mostramos la clave para no perder el alta
				redirect.addFlashAttribute("error", "El empleado se creó, pero no se pudo enviar el email. "
						+ "Su contraseña temporal es: " + passwordTemporal);
			}

			return "redirect:/responsable/empleados";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("festivales", festivalService.festivalesVigentesDe(unidad));

			return "responsable/form-empleado";
		}
	}

	@GetMapping("/empleados/{id}/editar")
	public String editarEmpleado(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirect) {

		try {

			Persona empleado = empleadoService.obtenerEmpleadoDeUnidad(id, unidadDe(principal));
			EmpleadoDTO dto = convertirEmpleadoDTO(empleado);

			model.addAttribute("empleadoDTO", dto);
			model.addAttribute("empleadoEditando", true);

			return "responsable/form-empleado";

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/responsable/empleados";
		}
	}

	@PostMapping("/empleados/{id}")
	public String actualizarEmpleado(@PathVariable Long id, Principal principal, @Valid @ModelAttribute("empleadoDTO") EmpleadoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {

			model.addAttribute("empleadoEditando", true);
			return "responsable/form-empleado";
		}

		try {

			empleadoService.actualizar(dto, id, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Empleado actualizado correctamente");

			return "redirect:/responsable/empleados";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("empleadoEditando", true);

			return "responsable/form-empleado";
		}
	}

	@PostMapping("/empleados/{id}/baja")
	public String bajaEmpleado(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {

		try {

			empleadoService.darDeBaja(id, unidadDe(principal));

			redirect.addFlashAttribute("mensaje", "Empleado dado de baja correctamente");

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/responsable/empleados";
	}

	@PostMapping("/empleados/{id}/reactivar")
	public String reactivarEmpleado(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {

		try {

			empleadoService.reactivar(id, unidadDe(principal));

			redirect.addFlashAttribute("mensaje", "Empleado reactivado correctamente");

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/responsable/empleados";
	}

	@GetMapping("/platos")
	public String platos(Principal principal, @RequestParam(required = false, defaultValue = "") String busqueda, Model model) {

		UnidadDeVenta unidad = unidadDe(principal);

		model.addAttribute("unidad", unidad);
		model.addAttribute("platos", platoService.buscar(unidad, busqueda));
		model.addAttribute("busqueda", busqueda);
		model.addAttribute("bajas", platoService.dadosDeBaja(unidad));

		return "responsable/platos";
	}

	@GetMapping("/platos/nuevo")
	public String nuevoPlato(Model model) {

		model.addAttribute("platoDTO", new PlatoDTO());

		return "responsable/form-plato";
	}

	@PostMapping("/platos")
	public String crearPlato(Principal principal, @Valid @ModelAttribute("platoDTO") PlatoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {
			return "responsable/form-plato";
		}

		try {

			platoService.crear(dto, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato creado correctamente");

			return "redirect:/responsable/platos";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());

			return "responsable/form-plato";
		}
	}

	@GetMapping("/platos/{id}/editar")
	public String editarPlato(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirect) {

		try {

			var plato = platoService.obtenerDeUnidad(id, unidadDe(principal));
			PlatoDTO dto = new PlatoDTO();

			dto.setId(plato.getId());
			dto.setNombre(plato.getNombre());
			dto.setPrecio(plato.getPrecio());
			dto.setCosto(plato.getCosto());

			model.addAttribute("platoDTO", dto);
			model.addAttribute("platoEditando", true);

			return "responsable/form-plato";

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());

			return "redirect:/responsable/platos";
		}
	}

	@PostMapping("/platos/{id}")
	public String actualizarPlato(@PathVariable Long id, Principal principal, @Valid @ModelAttribute("platoDTO") PlatoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {

			model.addAttribute("platoEditando", true);

			return "responsable/form-plato";
		}

		try {

			platoService.actualizar(id, dto, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato actualizado correctamente");

			return "redirect:/responsable/platos";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("platoEditando", true);

			return "responsable/form-plato";
		}
	}

	@PostMapping("/platos/{id}/baja")
	public String bajaPlato(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {

		try {

			platoService.darDeBaja(id, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato dado de baja correctamente");

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/responsable/platos";
	}

	@PostMapping("/platos/{id}/reactivar")
	public String reactivarPlato(@PathVariable Long id, Principal principal, RedirectAttributes redirect) {

		try {

			platoService.reactivar(id, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato reactivado correctamente");

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/responsable/platos";
	}

	private UnidadDeVenta unidadDe(Principal principal) {

		return unidadRepository
				.findByResponsable(
						usuarioService.getPersona(principal.getName()))
				.orElseThrow(() ->
						new IllegalStateException(
								"El responsable no tiene unidad asignada"));
	}

	private EmpleadoDTO convertirEmpleadoDTO(Persona empleado) {

		EmpleadoDTO dto = new EmpleadoDTO();

		dto.setId(empleado.getId());
		dto.setNombre(empleado.getNombre());
		dto.setApellido(empleado.getApellido());
		dto.setDni(empleado.getDni());
		dto.setEmail(empleado.getEmail());
		dto.setFechaNacimiento(empleado.getFechaNacimiento());

		if (empleado instanceof Cajero cajero) {

			dto.setTipo("CAJERO");
			dto.setTurno(cajero.getTurno());

		} else if (empleado instanceof Cocinero cocinero) {

			dto.setTipo("COCINERO");
			dto.setCategoria(cocinero.getCategoria());
			dto.setEspecialidad(cocinero.getEspecialidad());
		}

		return dto;
	}
}
