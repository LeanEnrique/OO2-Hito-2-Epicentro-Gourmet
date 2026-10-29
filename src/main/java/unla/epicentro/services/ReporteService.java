package unla.epicentro.services;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.ReporteVentaDTO;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.Pedido;
import unla.epicentro.repositories.PedidoRepository;

@Service
public class ReporteService {

    private final PedidoRepository pedidoRepository;

    public ReporteService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public ReporteVentaDTO calcularRecaudacion(Festival festival) {

        List<Pedido> pedidos = pedidoRepository.findByFestival(festival);

        BigDecimal recaudacion = pedidos.stream()
            .map(Pedido::getTotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ReporteVentaDTO(festival.getId(), festival.getNombre(), recaudacion);
    }
}