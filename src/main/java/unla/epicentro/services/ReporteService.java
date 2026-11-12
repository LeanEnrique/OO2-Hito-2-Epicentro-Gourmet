package unla.epicentro.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.RankingUnidadDTO;
import unla.epicentro.dtos.ReporteVentaDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.PedidoRepository;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;

    public ReporteService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public ReporteVentaDTO obtenerRecaudacion(Festival festival) {

        if (festival == null) {
            throw new IllegalArgumentException("El festival es obligatorio");
        }

        List<Pedido> pedidos = pedidoRepository.findByFestival(festival);

        BigDecimal total = pedidos.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReporteVentaDTO(
                festival.getId(),
                festival.getNombre(),
                total
        );
    }

    // Unidades del festival ordenadas de mayor a menor recaudación (las que no vendieron quedan al final con $ 0)
    public List<RankingUnidadDTO> rankingUnidades(Festival festival) {

        if (festival == null) {
            throw new IllegalArgumentException("El festival es obligatorio");
        }

        Map<Long, List<Pedido>> pedidosPorUnidad = pedidoRepository.findByFestival(festival).stream()
                .collect(Collectors.groupingBy(p -> p.getUnidad().getId()));

        List<UnidadDeVenta> unidades = festival.getUnidades().stream()
                .sorted(Comparator
                        .comparing((UnidadDeVenta u) -> recaudacion(pedidosPorUnidad.get(u.getId())))
                        .reversed()
                        .thenComparing(UnidadDeVenta::getNombreComercial))
                .toList();

        List<RankingUnidadDTO> ranking = new ArrayList<>();
        for (UnidadDeVenta unidad : unidades) {
            List<Pedido> pedidos = pedidosPorUnidad.getOrDefault(unidad.getId(), List.of());
            ranking.add(new RankingUnidadDTO(
                    ranking.size() + 1,
                    unidad.getNombreComercial(),
                    unidad.getCodigo(),
                    unidad.getTipo(),
                    pedidos.size(),
                    recaudacion(pedidos)));
        }
        return ranking;
    }

    private BigDecimal recaudacion(List<Pedido> pedidos) {
        if (pedidos == null) {
            return BigDecimal.ZERO;
        }
        return pedidos.stream()
                .map(Pedido::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
