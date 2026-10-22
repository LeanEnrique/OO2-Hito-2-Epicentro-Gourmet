package unla.epicentro.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import unla.epicentro.repositories.FestivalRepository;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final FestivalRepository festivalRepository;

	public AdminController(FestivalRepository festivalRepository) {
		this.festivalRepository = festivalRepository;
	}

	@GetMapping
	public String inicio(Model model) {
		model.addAttribute("festivales", festivalRepository.findAllByOrderByFechaInicioAsc());
		return "admin/inicio";
	}
}
