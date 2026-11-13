package unla.epicentro.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import unla.epicentro.dtos.LiquidacionDTO;
import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Persona;
import unla.epicentro.services.AsignacionService;
import unla.epicentro.services.CajaService;
import unla.epicentro.services.LiquidacionService;
import unla.epicentro.services.PedidoService;
import unla.epicentro.services.ReciboPdfService;
import unla.epicentro.services.UsuarioService;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

	private final UsuarioService usuarioService;
	private final LiquidacionService liquidacionService;
	private final ReciboPdfService reciboPdfService;
	private final AsignacionService asignacionService;
	private final CajaService cajaService;
	private final PedidoService pedidoService;

	public EmpleadoController(UsuarioService usuarioService, LiquidacionService liquidacionService,
			ReciboPdfService reciboPdfService, AsignacionService asignacionService, CajaService cajaService,
			PedidoService pedidoService) {
		this.usuarioService = usuarioService;
		this.liquidacionService = liquidacionService;
		this.reciboPdfService = reciboPdfService;
		this.asignacionService = asignacionService;
		this.cajaService = cajaService;
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public String inicio(Principal principal, Model model) {
		Persona empleado = usuarioService.getPersona(principal.getName());
		model.addAttribute("empleado", empleado);
		model.addAttribute("actual", asignacionService.actual(empleado).orElse(null));
		model.addAttribute("proxima", asignacionService.proxima(empleado).orElse(null));
		model.addAttribute("historial", asignacionService.historial(empleado));
		return "empleado/inicio";
	}

	// Liquidación de sueldo: sin festival elegido muestra el festival en curso, o el más reciente
	@GetMapping("/liquidacion")
	public String liquidacion(@RequestParam(required = false) Long festival, Principal principal, Model model) {
		Persona empleado = usuarioService.getPersona(principal.getName());
		List<LiquidacionDTO> liquidaciones = liquidacionService.liquidar(empleado);
		Long seleccionado = festival;
		if (seleccionado == null && !liquidaciones.isEmpty()) {
			seleccionado = asignacionService.actual(empleado)
					.map(a -> a.getFestival().getId())
					.orElse(liquidaciones.get(0).festivalId());
		}
		model.addAttribute("empleado", empleado);
		model.addAttribute("liquidaciones", liquidaciones);
		model.addAttribute("festivalSeleccionado", seleccionado);
		model.addAttribute("detalle", liquidacionService.detalle(empleado, seleccionado).orElse(null));
		return "empleado/liquidacion";
	}

	@GetMapping("/recibo")
	public ResponseEntity<byte[]> recibo(@RequestParam Long festival, Principal principal) {
		Persona empleado = usuarioService.getPersona(principal.getName());
		return liquidacionService.detalle(empleado, festival)
				.map(detalle -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_PDF)
						.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"recibo-sueldo-" + festival + ".pdf\"")
						.body(reciboPdfService.generar(empleado, detalle)))
				.orElse(ResponseEntity.notFound().build());
	}

	// CU Cerrar Caja: pantalla del cajero con la caja del día y su historial
	@GetMapping("/caja")
	public String caja(Principal principal, Model model, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		AsignacionPersonal asignacion = asignacionService.actual(cajero).orElse(null);
		Caja abierta = cajaService.abiertaDe(cajero).orElse(null);

		model.addAttribute("cajero", cajero);
		model.addAttribute("asignacion", asignacion);
		model.addAttribute("abierta", abierta);
		model.addAttribute("ventasDelDia", abierta == null ? BigDecimal.ZERO : cajaService.ventasDelDia(abierta));
		model.addAttribute("cantidadPedidos", abierta == null ? 0 : cajaService.pedidosDe(abierta).size());
		model.addAttribute("reanudable", abierta == null ? cajaService.reanudableDe(cajero).orElse(null) : null);
		model.addAttribute("historial", cajaService.historial(cajero));

		return "empleado/caja";
	}

	@PostMapping("/caja/abrir")
	public String abrirCaja(Principal principal, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			AsignacionPersonal asignacion = asignacionService.actual(cajero)
					.orElseThrow(() -> new IllegalArgumentException("Hoy no tenés un festival activo asignado"));
			cajaService.abrir(cajero, asignacion);
			redirect.addFlashAttribute("mensaje",
					"Caja abierta para el turno " + cajero.getTurno().getDescripcion() + ". Ya podés tomar pedidos.");
			return "redirect:/empleado/pedidos";
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/empleado/caja";
	}

	@PostMapping("/caja/cerrar")
	public String cerrarCaja(Principal principal, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			Caja caja = cajaService.cerrar(cajero);
			redirect.addFlashAttribute("mensaje",
					"Caja cerrada: " + caja.getCantidadPedidos() + (caja.getCantidadPedidos() == 1 ? " pedido" : " pedidos")
							+ " por $ " + pesos(caja.getVentasDelDia()) + ".");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/empleado/caja";
	}

	@PostMapping("/caja/reanudar")
	public String reanudarCaja(Principal principal, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			cajaService.reanudar(cajero);
			redirect.addFlashAttribute("mensaje", "Caja reanudada. Podés seguir tomando pedidos.");
			return "redirect:/empleado/pedidos";
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/empleado/caja";
	}

	// Historial de todos los pedidos del cajero; con la caja abierta puede cargar nuevos
	@GetMapping("/pedidos")
	public String pedidos(Principal principal, Model model, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		model.addAttribute("abierta", cajaService.abiertaDe(cajero).orElse(null));
		model.addAttribute("pedidos", pedidoService.pedidosDe(cajero));
		return "empleado/pedidos";
	}

	// Alta de pedido: la hace el cajero, solo con su caja abierta
	@GetMapping("/pedidos/nuevo")
	public String nuevoPedido(Principal principal, Model model, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		Caja abierta = cajaService.abiertaDe(cajero).orElse(null);
		if (abierta == null) {
			return "redirect:/empleado/pedidos";
		}

		model.addAttribute("caja", abierta);
		model.addAttribute("platos", pedidoService.platosDisponibles(abierta.getUnidad()));
		return "empleado/nuevo-pedido";
	}

	// Las cantidades llegan como parámetros "cant_<id del plato>"
	@PostMapping("/pedidos")
	public String crearPedido(Principal principal, @RequestParam Map<String, String> params, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			Map<Long, Integer> cantidades = new HashMap<>();
			params.forEach((clave, valor) -> {
				if (clave.startsWith("cant_") && !valor.isBlank()) {
					cantidades.put(Long.valueOf(clave.substring(5)), Integer.valueOf(valor));
				}
			});
			Pedido pedido = pedidoService.crear(cajaService.abiertaDe(cajero).orElse(null), cantidades);
			redirect.addFlashAttribute("mensaje", "Pedido #" + pedido.getId() + " registrado por $ " + pesos(pedido.getTotal()) + ".");
			return "redirect:/empleado/pedidos";
		} catch (NumberFormatException e) {
			redirect.addFlashAttribute("error", "Las cantidades tienen que ser números enteros.");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/empleado/pedidos/nuevo";
	}

	// Solo los cajeros administran la caja; si el empleado no lo es, vuelve con un aviso
	private Cajero cajeroDe(Principal principal, RedirectAttributes redirect) {
		Persona persona = usuarioService.getPersona(principal.getName());
		if (persona instanceof Cajero cajero) {
			return cajero;
		}
		redirect.addFlashAttribute("error", "La caja la administran solamente los cajeros.");
		return null;
	}

	// Formato de montos igual al de las pantallas: 1.234,50
	private String pesos(BigDecimal monto) {
		DecimalFormat formato = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(new Locale("es", "AR")));
		return formato.format(monto);
	}
}
