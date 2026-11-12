package unla.epicentro.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.dtos.ReporteRentabilidadDTO;
import unla.epicentro.dtos.PlatoDTO;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.services.CanonService;
import unla.epicentro.services.PlatoEstrellaService;
import unla.epicentro.services.ReporteService;
import unla.epicentro.services.RentabilidadService;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.entities.UnidadDeVenta;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final FestivalRepository festivalRepository;
	private final CanonService canonService;
	private final ReporteService reporteService;
	private final RentabilidadService rentabilidadService;
	private final PlatoEstrellaService platoEstrellaService;

	public AdminController(FestivalRepository festivalRepository, CanonService canonService, ReporteService reporteService, RentabilidadService rentabilidadService, PlatoEstrellaService platoEstrellaService) {
		this.festivalRepository = festivalRepository;
		this.canonService = canonService;
		this.reporteService = reporteService;
		this.rentabilidadService = rentabilidadService;
		this.platoEstrellaService = platoEstrellaService;
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

	if (seleccionado != null) {
    model.addAttribute(
            "reporteVenta",
            reporteService.obtenerRecaudacion(seleccionado)
    );
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

	@GetMapping("/rentabilidad")
public String rentabilidad(
        @RequestParam(required = false) Long festivalId,
        @RequestParam(required = false) Long unidadId,
        @RequestParam(required = false) LocalDate desde,
        @RequestParam(required = false) LocalDate hasta,
        Model model) {

    List<Festival> festivales =
            festivalRepository.findAllByOrderByFechaInicioAsc();

    model.addAttribute("festivales", festivales);

    Festival festival = festivalId == null
            ? null
            : festivalRepository.findById(festivalId).orElse(null);

    model.addAttribute("festivalSeleccionado", festival);

    if (festival != null) {

        model.addAttribute("unidades", festival.getUnidades());

        model.addAttribute("unidadSeleccionada", unidadId);

        if (unidadId != null) {

            UnidadDeVenta unidad = festival.getUnidades()
                    .stream()
                    .filter(u -> u.getId().equals(unidadId))
                    .findFirst()
                    .orElse(null);

            if (unidad == null) {
                model.addAttribute("error",
                        "La unidad no pertenece al festival seleccionado.");

            } else {
                try {

                    ReporteRentabilidadDTO reporte;

                    if (desde == null && hasta == null) {

                        reporte = rentabilidadService.calcularRentabilidad(
                                festival, unidad);

                    } else {

                        reporte = rentabilidadService.calcularRentabilidadEntreFechas(
                                festival, unidad, desde, hasta);
                    }

                    model.addAttribute("reporte", reporte);

                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", e.getMessage());
                }
            }
        }
    }

    model.addAttribute("desde", desde);
    model.addAttribute("hasta", hasta);

    return "admin/rentabilidad";
}

@GetMapping("/plato-estrella")
public String platoEstrella(
        @RequestParam(required = false) Long festivalId,
        @RequestParam(required = false) Long unidadId,
        Model model) {

    List<Festival> festivales =
            festivalRepository.findAllByOrderByFechaInicioAsc();

    model.addAttribute("festivales", festivales);
	model.addAttribute("unidadId", unidadId);

    Festival festival = festivalId == null
            ? null
            : festivalRepository.findById(festivalId).orElse(null);

    model.addAttribute("festivalSeleccionado", festival);

    if (festival != null) {

        model.addAttribute("unidades", festival.getUnidades());

        if (unidadId != null) {

            UnidadDeVenta unidad = festival.getUnidades()
                    .stream()
                    .filter(u -> u.getId().equals(unidadId))
                    .findFirst()
                    .orElse(null);

            if (unidad == null) {

                model.addAttribute("error",
                        "La unidad no pertenece al festival seleccionado.");

            } else {

                PlatoDTO plato = platoEstrellaService
                        .obtenerPlatoEstrella(unidad, festival);

                model.addAttribute("platoEstrella", plato);
            }
        }
    }

    return "admin/plato-estrella";
}

}
