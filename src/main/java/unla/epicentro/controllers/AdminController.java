package unla.epicentro.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.entities.Festival;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.services.CanonService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final FestivalRepository festivalRepository;
	private final CanonService canonService;

	public AdminController(FestivalRepository festivalRepository, CanonService canonService) {
		this.festivalRepository = festivalRepository;
		this.canonService = canonService;
	}

	@GetMapping
public String inicio(
        @RequestParam(required = false) Long festival,
        @RequestParam(defaultValue = "3") int x,
        Model model) {

    List<Festival> festivales = festivalRepository.findAllByOrderByFechaInicioAsc();

    Festival seleccionado = festival == null
            ? porDefecto(festivales)
            : festivales.stream()
                    .filter(f -> f.getId().equals(festival))
                    .findFirst()
                    .orElse(null);

    model.addAttribute("festivales", festivales);
    model.addAttribute("seleccionado", seleccionado);
    model.addAttribute("x", x);

    if (seleccionado != null && x > 0) {
        model.addAttribute(
                "mayoresCanon",
                canonService.obtenerMayoresCanon(seleccionado, x));
    }

    return "admin/inicio";
}
	/*
	public String inicio(@RequestParam(required = false) Long festival, Model model) {
		List<Festival> festivales = festivalRepository.findAllByOrderByFechaInicioAsc();
		Festival seleccionado = festival == null ? porDefecto(festivales)
				: festivales.stream().filter(f -> f.getId().equals(festival)).findFirst().orElse(null);
		model.addAttribute("festivales", festivales);
		model.addAttribute("seleccionado", seleccionado);
		return "admin/inicio";
	}

	*/
	// Sin festival elegido se muestra el que está en curso hoy, o el primero si no hay ninguno en curso
	private Festival porDefecto(List<Festival> festivales) {
		LocalDate hoy = LocalDate.now();
		return festivales.stream().filter(f -> f.estaActivo(hoy)).findFirst()
				.orElse(festivales.isEmpty() ? null : festivales.get(0));
	}
}
