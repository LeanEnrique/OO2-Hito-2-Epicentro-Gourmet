package unla.epicentro.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

	@GetMapping("/login")
	public String login(Authentication auth) {
		if (auth != null) {
			return "redirect:/home";
		}
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

	private boolean tieneRol(Authentication auth, String rol) {
		return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals(rol));
	}
}
