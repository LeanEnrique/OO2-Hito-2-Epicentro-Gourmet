package unla.epicentro.controllers;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.dtos.DetalleLiquidacionDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.services.FestivalService;
import unla.epicentro.services.LiquidacionService;
import unla.epicentro.services.PlatoEstrellaService;
import unla.epicentro.services.RentabilidadService;
import unla.epicentro.services.ReporteService;
import unla.epicentro.services.UsuarioService;

// Reportes que el enunciado asigna al Responsable: siempre acotados a SU unidad de venta
@Controller
@RequestMapping("/responsable/reportes")
public class ReporteResponsableController {

	private final UsuarioService usuarioService;
	private final UnidadDeVentaRepository unidadRepository;
	private final FestivalService festivalService;
	private final ReporteService reporteService;
	private final RentabilidadService rentabilidadService;
	private final PlatoEstrellaService platoEstrellaService;
	private final LiquidacionService liquidacionService;

	public ReporteResponsableController(UsuarioService usuarioService, UnidadDeVentaRepository unidadRepository,
			FestivalService festivalService, ReporteService reporteService, RentabilidadService rentabilidadService,
			PlatoEstrellaService platoEstrellaService, LiquidacionService liquidacionService) {
		this.usuarioService = usuarioService;
		this.unidadRepository = unidadRepository;
		this.festivalService = festivalService;
		this.reporteService = reporteService;
		this.rentabilidadService = rentabilidadService;
		this.platoEstrellaService = platoEstrellaService;
		this.liquidacionService = liquidacionService;
	}

	// CU Reporte de Recaudación (ReporteVentaDTO): recaudación del festival y la parte de mi unidad
	@GetMapping
	public String recaudacion(@RequestParam(required = false) Long festivalId, Principal principal, Model model) {

		UnidadDeVenta unidad = unidadDe(principal);
		cargarFestivales(unidad, model);

		Festival festival = buscarFestival(unidad, festivalId, model);
		if (festival == null) {
			return "responsable/reportes";
		}

		model.addAttribute("festival", festival);
		model.addAttribute("reporte", reporteService.obtenerRecaudacion(festival));
		model.addAttribute("recaudacionUnidad", reporteService.recaudacionDeUnidad(festival, unidad));

		return "responsable/reportes";
	}

	// CU Cálculo de Rentabilidad Neta y entre dos fechas
	@GetMapping("/rentabilidad")
	public String rentabilidad(@RequestParam(required = false) Long festivalId,
			@RequestParam(required = false) LocalDate desde,
			@RequestParam(required = false) LocalDate hasta,
			Principal principal, Model model) {

		UnidadDeVenta unidad = unidadDe(principal);
		cargarFestivales(unidad, model);
		model.addAttribute("desde", desde);
		model.addAttribute("hasta", hasta);

		Festival festival = buscarFestival(unidad, festivalId, model);
		if (festival == null) {
			return "responsable/reportes-rentabilidad";
		}

		model.addAttribute("festival", festival);

		try {
			if (desde == null && hasta == null) {
				model.addAttribute("reporte", rentabilidadService.calcularRentabilidad(festival, unidad));
			} else {
				model.addAttribute("reporte",
						rentabilidadService.calcularRentabilidadEntreFechas(festival, unidad, desde, hasta));
			}
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
		}

		return "responsable/reportes-rentabilidad";
	}

	// CU Plato Estrella (PlatoDTO)
	@GetMapping("/plato-estrella")
	public String platoEstrella(@RequestParam(required = false) Long festivalId, Principal principal, Model model) {

		UnidadDeVenta unidad = unidadDe(principal);
		cargarFestivales(unidad, model);

		Festival festival = buscarFestival(unidad, festivalId, model);
		if (festival == null) {
			return "responsable/reportes-plato-estrella";
		}

		model.addAttribute("festival", festival);

		try {
			model.addAttribute("platoEstrella", platoEstrellaService.obtenerPlatoEstrella(unidad, festival));
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
		}

		return "responsable/reportes-plato-estrella";
	}

	// CU Liquidación de Haberes del personal de mi unidad en un festival
	@GetMapping("/liquidacion")
	public String liquidacion(@RequestParam(required = false) Long festivalId, Principal principal, Model model) {

		UnidadDeVenta unidad = unidadDe(principal);
		cargarFestivales(unidad, model);

		Festival festival = buscarFestival(unidad, festivalId, model);
		if (festival == null) {
			return "responsable/reportes-liquidacion";
		}

		List<DetalleLiquidacionDTO> liquidaciones = liquidacionService.liquidarPersonal(festival, unidad);
		BigDecimal totalNomina = liquidaciones.stream()
				.map(DetalleLiquidacionDTO::total)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		model.addAttribute("festival", festival);
		model.addAttribute("liquidaciones", liquidaciones);
		model.addAttribute("totalNomina", totalNomina);

		return "responsable/reportes-liquidacion";
	}

	private void cargarFestivales(UnidadDeVenta unidad, Model model) {
		model.addAttribute("unidad", unidad);
		model.addAttribute("festivales", festivalService.festivalesDe(unidad));
	}

	// Solo se permite consultar festivales en los que la unidad está inscripta
	private Festival buscarFestival(UnidadDeVenta unidad, Long festivalId, Model model) {
		if (festivalId == null) {
			return null;
		}
		try {
			return festivalService.festivalDe(unidad, festivalId);
		} catch (IllegalArgumentException e) {
			model.addAttribute("error", e.getMessage());
			return null;
		}
	}

	private UnidadDeVenta unidadDe(Principal principal) {
		return unidadRepository
				.findByResponsable(usuarioService.getPersona(principal.getName()))
				.orElseThrow(() -> new IllegalStateException("El responsable no tiene unidad asignada"));
	}
}
