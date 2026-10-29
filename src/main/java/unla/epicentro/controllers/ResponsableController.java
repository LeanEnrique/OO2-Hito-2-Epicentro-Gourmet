package unla.epicentro.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;

import unla.epicentro.dtos.CanonDTO;
import unla.epicentro.dtos.EmpleadoDTO;
import unla.epicentro.dtos.PlatoDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Cocinero;
import unla.epicentro.entities.Persona;
import unla.epicentro.repositories.PlatoRepository;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.services.PedidoService;
import unla.epicentro.services.UsuarioService;
import unla.epicentro.services.EmpleadoService;
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
	private final FestivalRepository festivalRepository;
	private final CanonService canonService;

	public ResponsableController(UsuarioService usuarioService, UnidadDeVentaRepository unidadRepository, PlatoRepository platoRepository, PedidoService pedidoService, EmpleadoService empleadoService, PlatoService platoService, FestivalRepository festivalRepository) {

		this.usuarioService = usuarioService;
		this.unidadRepository = unidadRepository;
		this.platoRepository = platoRepository;
		this.pedidoService = pedidoService;
		this.empleadoService = empleadoService;
		this.platoService = platoService;
		this.festivalRepository = festivalRepository;
		this.canonService = canonService;
	}

	@GetMapping
	public String inicio(Principal principal, Model model) {
		UnidadDeVenta unidad = unidadDe(principal);
		model.addAttribute("unidad", unidad);
		model.addAttribute("platos", platoRepository.findByUnidadAndActivoTrueOrderByNombreAsc(unidad));
		model.addAttribute("pedidos", pedidoService.pedidosDe(unidad));
		return "responsable/inicio";
	}

	@GetMapping("/pedidos/nuevo")
	public String nuevoPedido(Principal principal, Model model) {
		UnidadDeVenta unidad = unidadDe(principal);
		model.addAttribute("unidad", unidad);
		model.addAttribute("festivales", pedidoService.festivalesActivos(unidad));
		model.addAttribute("platos", platoRepository.findByUnidadAndActivoTrueOrderByNombreAsc(unidad));
		return "responsable/nuevo-pedido";
	}

	// Las cantidades llegan como parámetros "cant_<id|Plato>"
	@PostMapping("/pedidos")
	public String crearPedido(Principal principal, @RequestParam Long festivalId, @RequestParam Map<String, String> params, RedirectAttributes redirect) {
		try {
			Map<Long, Integer> cantidades = new HashMap<>();
			params.forEach((clave, valor) -> {
				if (clave.startsWith("cant_") && !valor.isBlank()) {
					cantidades.put(Long.valueOf(clave.substring(5)), Integer.valueOf(valor));
				}
			});
			pedidoService.crear(unidadDe(principal), festivalId, cantidades);
			redirect.addFlashAttribute("mensaje", "Pedido registrado");
			return "redirect:/responsable";
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/responsable/pedidos/nuevo";
		}
	}
@GetMapping("/empleados")
	public String empleados(
			Principal principal,
			@RequestParam(required = false, defaultValue = "") String busqueda,
			Model model) {

		UnidadDeVenta unidad = unidadDe(principal);

		model.addAttribute("unidad", unidad);
		model.addAttribute(
				"empleados",
				empleadoService.buscar(unidad, busqueda));
		model.addAttribute("busqueda", busqueda);

		return "responsable/empleados";
	}

	@GetMapping("/empleados/nuevo")
	public String nuevoEmpleado(Model model) {

		model.addAttribute("empleadoDTO", new EmpleadoDTO());
		model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());

		return "responsable/empleado-form";
	}

	@PostMapping("/empleados")
	public String crearEmpleado(Principal principal, @Valid @ModelAttribute("empleadoDTO") EmpleadoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {

			model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());
			return "responsable/empleado-form";
		}

		try {
			empleadoService.crear(dto, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Empleado creado correctamente. Se enviaron sus datos de acceso por email.");

			return "redirect:/responsable/empleados";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());

			return "responsable/empleado-form";
		}
	}

	@GetMapping("/empleados/{id}/editar")
	public String editarEmpleado(@PathVariable Long id, Principal principal, Model model, RedirectAttributes redirect) {

		try {

			Persona empleado = empleadoService.obtenerEmpleadoDeUnidad(id, unidadDe(principal));
			EmpleadoDTO dto = convertirEmpleadoDTO(empleado);

			model.addAttribute("empleadoDTO", dto);
			model.addAttribute("empleadoEditando", true);

			return "responsable/empleado-form";

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());
			return "redirect:/responsable/empleados";
		}
	}

	@PostMapping("/empleados/{id}")
	public String actualizarEmpleado(@PathVariable Long id, Principal principal, @Valid @ModelAttribute("empleadoDTO") EmpleadoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {

			model.addAttribute("empleadoEditando", true);
			return "responsable/empleado-form";
		}

		try {

			empleadoService.actualizar(dto, id, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Empleado actualizado correctamente");

			return "redirect:/responsable/empleados";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("empleadoEditando", true);

			return "responsable/empleado-form";
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

		return "responsable/platos";
	}

	@GetMapping("/platos/nuevo")
	public String nuevoPlato(Model model) {

		model.addAttribute("platoDTO", new PlatoDTO());

		return "responsable/plato-form";
	}

	@PostMapping("/platos")
	public String crearPlato(Principal principal, @Valid @ModelAttribute("platoDTO") PlatoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {
			return "responsable/plato-form";
		}

		try {

			platoService.crear(dto, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato creado correctamente");

			return "redirect:/responsable/platos";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());

			return "responsable/plato-form";
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

			return "responsable/plato-form";

		} catch (IllegalArgumentException e) {

			redirect.addFlashAttribute("error", e.getMessage());

			return "redirect:/responsable/platos";
		}
	}

	@PostMapping("/platos/{id}")
	public String actualizarPlato(@PathVariable Long id, Principal principal, @Valid @ModelAttribute("platoDTO") PlatoDTO dto, BindingResult result, Model model, RedirectAttributes redirect) {

		if (result.hasErrors()) {

			model.addAttribute("platoEditando", true);

			return "responsable/plato-form";
		}

		try {

			platoService.actualizar(id, dto, unidadDe(principal));
			redirect.addFlashAttribute("mensaje", "Plato actualizado correctamente");

			return "redirect:/responsable/platos";

		} catch (IllegalArgumentException e) {

			model.addAttribute("error", e.getMessage());
			model.addAttribute("platoEditando", true);

			return "responsable/plato-form";
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

	@GetMapping("/canon")
	public String canon(
        @RequestParam Long festivalId,
        Principal principal,
        Model model) {

    Festival festival = festivalRepository.findById(festivalId)
        .orElseThrow(() -> new IllegalArgumentException("Festival no encontrado"));

    UnidadDeVenta unidad = unidadDe(principal);

    if (!festival.getUnidades().contains(unidad)) {
        throw new IllegalArgumentException("La unidad no pertenece al festival seleccionado");
    }

    CanonDTO canon = canonService.obtenerCanon(unidad, festival);

    model.addAttribute("festival", festival);
    model.addAttribute("canon", canon);

    return "responsable/canon";
}}
