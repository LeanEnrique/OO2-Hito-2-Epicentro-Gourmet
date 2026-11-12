package unla.epicentro.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import jakarta.servlet.http.HttpServletRequest;

// Datos que necesitan todas las vistas (por ejemplo, la barra de navegación)
@ControllerAdvice
public class VistaGlobalAdvice {

	// Ruta actual sin el context path, para resaltar la sección activa en la barra
	@ModelAttribute("rutaActual")
	public String rutaActual(HttpServletRequest request) {
		return request.getRequestURI().substring(request.getContextPath().length());
	}
}
