package unla.epicentro.controllers;

import java.security.Principal;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.entities.Persona;
import unla.epicentro.services.AsignacionService;
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

	public EmpleadoController(UsuarioService usuarioService, LiquidacionService liquidacionService,
			ReciboPdfService reciboPdfService, AsignacionService asignacionService) {
		this.usuarioService = usuarioService;
		this.liquidacionService = liquidacionService;
		this.reciboPdfService = reciboPdfService;
		this.asignacionService = asignacionService;
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
}
