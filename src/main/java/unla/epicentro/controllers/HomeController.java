package unla.epicentro.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import unla.epicentro.dtos.RegistroUnidadDTO;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.services.MailService;
import unla.epicentro.services.RegistroService;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	private final FestivalRepository festivalRepository;
	private final RegistroService registroService;
	private final MailService mailService;

	public HomeController(FestivalRepository festivalRepository, RegistroService registroService,
			MailService mailService) {
		this.festivalRepository = festivalRepository;
		this.registroService = registroService;
		this.mailService = mailService;
	}

	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());
		return "index";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	// Después del login, cada rol va a su pantalla de inicio
	@GetMapping("/home")
	public String home(Authentication auth) {
		if (tieneRol(auth, "ROLE_ADMINISTRADOR")) {
			return "redirect:/admin";
		}
		if (tieneRol(auth, "ROLE_RESPONSABLE")) {
			return "redirect:/responsable";
		}
		return "redirect:/empleado";
	}

	@GetMapping("/registro")
	public String registroForm(Model model) {
		model.addAttribute("registro", new RegistroUnidadDTO());
		model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());
		return "registro";
	}

	@PostMapping("/registro")
	public String registrar(@Valid @ModelAttribute("registro") RegistroUnidadDTO registro, BindingResult result,
			Model model, RedirectAttributes redirect) {
		if (!result.hasErrors()) {
			try {
				UnidadDeVenta unidad = registroService.registrar(registro);
				boolean mailEnviado = mailService.enviar(registro.getEmail(), "Bienvenido a Epicentro Gourmet",
						"Hola " + registro.getNombre() + ", registraste la unidad " + unidad.getNombreComercial()
								+ " (" + unidad.getCodigo() + ").\nTu usuario es: " + registro.getUsername());
				redirect.addFlashAttribute("mensaje", "Unidad registrada. Ya podés iniciar sesión."
						+ (mailEnviado ? " Te enviamos un mail de bienvenida." : ""));
				return "redirect:/login";
			} catch (IllegalArgumentException e) {
				model.addAttribute("error", e.getMessage());
			}
		}
		model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());
		return "registro";
	}

	private boolean tieneRol(Authentication auth, String rol) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
	}
}
