package unla.epicentro.controllers;

import java.math.BigDecimal;
import java.security.Principal;

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

import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Caja;
import unla.epicentro.entities.Cajero;
import unla.epicentro.entities.Persona;
import unla.epicentro.services.AsignacionService;
import unla.epicentro.services.CajaService;
import unla.epicentro.services.LiquidacionService;
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

	public EmpleadoController(UsuarioService usuarioService, LiquidacionService liquidacionService,
			ReciboPdfService reciboPdfService, AsignacionService asignacionService, CajaService cajaService) {
		this.usuarioService = usuarioService;
		this.liquidacionService = liquidacionService;
		this.reciboPdfService = reciboPdfService;
		this.asignacionService = asignacionService;
		this.cajaService = cajaService;
	}

	@GetMapping
	public String inicio(@RequestParam(required = false) Long festival, Principal principal, Model model) {
		Persona empleado = usuarioService.getPersona(principal.getName());
		model.addAttribute("empleado", empleado);
		model.addAttribute("actual", asignacionService.actual(empleado).orElse(null));
		model.addAttribute("proxima", asignacionService.proxima(empleado).orElse(null));
		model.addAttribute("historial", asignacionService.historial(empleado));
		model.addAttribute("liquidaciones", liquidacionService.liquidar(empleado));
		model.addAttribute("detalle", liquidacionService.detalle(empleado, festival).orElse(null));
		model.addAttribute("festivalSeleccionado", festival);
		model.addAttribute("esCajero", empleado instanceof Cajero);
		return "empleado/inicio";
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
		model.addAttribute("historial", cajaService.historial(cajero));

		return "empleado/caja";
	}

	@PostMapping("/caja/abrir")
	public String abrirCaja(Principal principal, @RequestParam BigDecimal montoInicial, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			AsignacionPersonal asignacion = asignacionService.actual(cajero)
					.orElseThrow(() -> new IllegalArgumentException("Hoy no tenés un festival activo asignado"));
			cajaService.abrir(cajero, asignacion, montoInicial);
			redirect.addFlashAttribute("mensaje",
					"Caja abierta para el turno " + cajero.getTurno().getDescripcion() + ".");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/empleado/caja";
	}

	@PostMapping("/caja/cerrar")
	public String cerrarCaja(Principal principal, @RequestParam BigDecimal montoDeclarado,
			@RequestParam(required = false) String observaciones, RedirectAttributes redirect) {

		Cajero cajero = cajeroDe(principal, redirect);
		if (cajero == null) {
			return "redirect:/empleado";
		}

		try {
			Caja caja = cajaService.cerrar(cajero, montoDeclarado, observaciones);
			redirect.addFlashAttribute("mensaje",
					"Caja cerrada. Vendiste $ " + caja.getVentasDelDia() + " y la diferencia de arqueo fue $ "
							+ caja.getDiferencia() + ".");
		} catch (IllegalArgumentException e) {
			redirect.addFlashAttribute("error", e.getMessage());
		}

		return "redirect:/empleado/caja";
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
}
