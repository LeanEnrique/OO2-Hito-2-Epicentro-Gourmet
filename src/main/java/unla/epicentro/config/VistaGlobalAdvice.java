package unla.epicentro.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;
import unla.epicentro.entities.Cajero;
import unla.epicentro.services.UsuarioService;

// Datos que necesitan todas las vistas (por ejemplo, la barra de navegación)
@ControllerAdvice
public class VistaGlobalAdvice {

	private final UsuarioService usuarioService;

	public VistaGlobalAdvice(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	// Ruta actual sin el context path, para resaltar la sección activa en la barra
	@ModelAttribute("rutaActual")
	public String rutaActual(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}

	// Solo los cajeros ven la opción Cerrar caja
	@ModelAttribute("esCajero")
	public boolean esCajero(HttpServletRequest request) {
		if (request.getUserPrincipal() == null || !request.isUserInRole("EMPLEADO")) {
			return false;
		}
		return usuarioService.getPersona(request.getUserPrincipal().getName()) instanceof Cajero;
	}
}
