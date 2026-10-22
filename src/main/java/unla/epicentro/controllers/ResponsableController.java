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

import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.PlatoRepository;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.services.PedidoService;
import unla.epicentro.services.UsuarioService;

@Controller
@RequestMapping("/responsable")
public class ResponsableController {

	private final UsuarioService usuarioService;
	private final UnidadDeVentaRepository unidadRepository;
	private final PlatoRepository platoRepository;
	private final PedidoService pedidoService;

	public ResponsableController(UsuarioService usuarioService, UnidadDeVentaRepository unidadRepository,
			PlatoRepository platoRepository, PedidoService pedidoService) {
		this.usuarioService = usuarioService;
		this.unidadRepository = unidadRepository;
		this.platoRepository = platoRepository;
		this.pedidoService = pedidoService;
	}

	@GetMapping
	public String inicio(Principal principal, Model model) {
		UnidadDeVenta unidad = unidadDe(principal);
		model.addAttribute("unidad", unidad);
		model.addAttribute("platos", platoRepository.findByUnidadAndActivoTrue(unidad));
		model.addAttribute("pedidos", pedidoService.pedidosDe(unidad));
		return "responsable/inicio";
	}

	@GetMapping("/pedidos/nuevo")
	public String nuevoPedido(Principal principal, Model model) {
		UnidadDeVenta unidad = unidadDe(principal);
		model.addAttribute("unidad", unidad);
		model.addAttribute("festivales", pedidoService.festivalesActivos(unidad));
		model.addAttribute("platos", platoRepository.findByUnidadAndActivoTrue(unidad));
		return "responsable/nuevo-pedido";
	}

	// Las cantidades llegan como parámetros "cant_<idPlato>"
	@PostMapping("/pedidos")
	public String crearPedido(Principal principal, @RequestParam Long festivalId,
			@RequestParam Map<String, String> params, RedirectAttributes redirect) {
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

	private UnidadDeVenta unidadDe(Principal principal) {
		return unidadRepository.findByResponsable(usuarioService.getPersona(principal.getName()))
				.orElseThrow(() -> new IllegalStateException("El responsable no tiene unidad asignada"));
	}
}
