package unla.epicentro.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.ReporteRentabilidadDTO;
import unla.epicentro.entities.AsignacionPersonal;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.ItemPlato;
import unla.epicentro.entities.Pedido;
import unla.epicentro.entities.UnidadDeVenta;
import unla.epicentro.repositories.AsignacionPersonalRepository;
import unla.epicentro.repositories.PedidoRepository;

@Service
public class RentabilidadService {

    private final PedidoRepository pedidoRepository;
    private final AsignacionPersonalRepository asignacionPersonalRepository;
    private final CanonService canonService;

    public RentabilidadService(
            PedidoRepository pedidoRepository,
            AsignacionPersonalRepository asignacionPersonalRepository,
            CanonService canonService) {

        this.pedidoRepository = pedidoRepository;
        this.asignacionPersonalRepository = asignacionPersonalRepository;
        this.canonService = canonService;
    }

    // Rentabilidad de una unidad durante todo el festival
    public ReporteRentabilidadDTO calcularRentabilidad(
            Festival festival,
            UnidadDeVenta unidad) {

        validarDatos(festival, unidad);

        List<Pedido> pedidos =
                pedidoRepository.findByFestivalAndUnidad(festival, unidad);

        return calcular(festival, unidad, pedidos);
    }

    // Rentabilidad de una unidad entre dos fechas
    public ReporteRentabilidadDTO calcularRentabilidadEntreFechas(
            Festival festival,
            UnidadDeVenta unidad,
            LocalDate desde,
            LocalDate hasta) {

        validarDatos(festival, unidad);

        if (desde == null || hasta == null || desde.isAfter(hasta)) {
            throw new IllegalArgumentException("El rango de fechas no es válido");
        }

        if (desde.isBefore(festival.getFechaInicio())
                || hasta.isAfter(festival.getFechaFin())) {
            throw new IllegalArgumentException(
                    "Las fechas deben estar dentro del festival");
        }

        List<Pedido> pedidos =
                pedidoRepository.findByFestivalAndUnidadAndFechaBetween(
                        festival, unidad, desde, hasta);

        return calcular(festival, unidad, pedidos);
    }

    private ReporteRentabilidadDTO calcular(
            Festival festival,
            UnidadDeVenta unidad,
            List<Pedido> pedidos) {

        BigDecimal ingresos = BigDecimal.ZERO;
        BigDecimal costosPlatos = BigDecimal.ZERO;

        for (Pedido pedido : pedidos) {

            ingresos = ingresos.add(pedido.getTotal());

            for (ItemPlato item : pedido.getItems()) {
                BigDecimal costo = item.getCostoUnitario()
                        .multiply(BigDecimal.valueOf(item.getCantidad()));

                costosPlatos = costosPlatos.add(costo);
            }
        }

        BigDecimal sueldos = asignacionPersonalRepository
                .findByFestivalAndUnidad(festival, unidad)
                .stream()
                .map(asignacion -> asignacion.getPersona().calcularSueldo(festival))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal canon = canonService.calcularCanon(unidad, festival);

        BigDecimal rentabilidadNeta = ingresos
                .subtract(costosPlatos)
                .subtract(sueldos)
                .subtract(canon);

        return new ReporteRentabilidadDTO(
                ingresos,
                costosPlatos,
                sueldos,
                canon,
                rentabilidadNeta);
    }

    private void validarDatos(Festival festival, UnidadDeVenta unidad) {

        if (festival == null) {
            throw new IllegalArgumentException("El festival es obligatorio");
        }

        if (unidad == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria");
        }

        if (!festival.getUnidades().contains(unidad)) {
            throw new IllegalArgumentException(
                    "La unidad no pertenece al festival seleccionado");
        }
    }
}