package unla.epicentro.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import unla.epicentro.entities.Festival;
import unla.epicentro.entities.ItemPlato;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.Plato;
import unla.epicentro.repositories.PedidoRepository;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReporteServiceTest {

    @Test
    void calcularRecaudacionSumaTodosLosPedidosDelFestival() {

        PedidoRepository pedidoRepository = mock(PedidoRepository.class);

        ReporteService reporteService = new ReporteService(pedidoRepository);

        Festival festival = new Festival();
        festival.setId(1L);
        festival.setNombre("Festival Gourmet");

        Plato plato = new Plato();
        plato.setPrecio(new BigDecimal("1000"));
        plato.setCosto(new BigDecimal("500"));

        Pedido pedido1 = new Pedido();
        pedido1.setFestival(festival);
        pedido1.agregarItem(plato, 2);

        Pedido pedido2 = new Pedido();
        pedido2.setFestival(festival);
        pedido2.agregarItem(plato, 3);

        when(pedidoRepository.findByFestival(festival))
            .thenReturn(List.of(pedido1, pedido2));

        var resultado = reporteService.calcularRecaudacion(festival);

        assertEquals(new BigDecimal("5000"), resultado.getRecaudacionTotal());
    }
}