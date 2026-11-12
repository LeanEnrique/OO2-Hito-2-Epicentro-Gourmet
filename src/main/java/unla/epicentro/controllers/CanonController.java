package unla.epicentro.controllers;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import unla.epicentro.entities.Festival;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.FestivalRepository;
import unla.epicentro.repositories.UnidadDeVentaRepository;
import unla.epicentro.services.CanonService;
import unla.epicentro.services.UsuarioService;

@Controller
public class CanonController {

    private final UsuarioService usuarioService;
    private final UnidadDeVentaRepository unidadRepository;
    private final FestivalRepository festivalRepository;
    private final CanonService canonService;

    public CanonController(
            UsuarioService usuarioService,
            UnidadDeVentaRepository unidadRepository,
            FestivalRepository festivalRepository,
            CanonService canonService) {

        this.usuarioService = usuarioService;
        this.unidadRepository = unidadRepository;
        this.festivalRepository = festivalRepository;
        this.canonService = canonService;
    }

    @GetMapping("/canon")
    public String canon(
            @RequestParam(required = false) Long festivalId,
            Principal principal,
            Model model) {

        UnidadDeVenta unidad = unidadRepository
                .findByResponsable(usuarioService.getPersona(principal.getName()))
                .orElseThrow(() -> new IllegalStateException(
                        "El responsable no tiene una unidad asignada"));

        List<Festival> festivales =
                festivalRepository.findByUnidadesContainingOrderByFechaInicioAsc(unidad);

        model.addAttribute("festivales", festivales);

        if (festivalId == null) {
            return "responsable/canon";
        }

        // Solo se calcula para festivales en los que participa la unidad
        Festival festival = festivales.stream()
                .filter(f -> f.getId().equals(festivalId))
                .findFirst()
                .orElse(null);
        if (festival == null) {
            model.addAttribute("error", "Tu unidad no está inscripta en ese festival");
            return "responsable/canon";
        }

        try {
            model.addAttribute("canon", canonService.obtenerCanon(unidad, festival));
            model.addAttribute("festival", festival);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
        }

        return "responsable/canon";
    }
}