package unla.epicentro.controllers;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.entities.Persona;
import unla.epicentro.services.LiquidacionService;
import unla.epicentro.services.UsuarioService;

@Controller
@RequestMapping("/empleado")
public class EmpleadoController {

	private final UsuarioService usuarioService;
	private final LiquidacionService liquidacionService;

	public EmpleadoController(UsuarioService usuarioService, LiquidacionService liquidacionService) {
		this.usuarioService = usuarioService;
		this.liquidacionService = liquidacionService;
	}

	@GetMapping
	public String inicio(@RequestParam(required = false) Long festival, Principal principal, Model model) {
		Persona empleado = usuarioService.getPersona(principal.getName());
		model.addAttribute("empleado", empleado);
		model.addAttribute("liquidaciones", liquidacionService.liquidar(empleado));
		model.addAttribute("detalle", liquidacionService.detalle(empleado, festival).orElse(null));
		model.addAttribute("festivalSeleccionado", festival);
		return "empleado/inicio";
	}
}
