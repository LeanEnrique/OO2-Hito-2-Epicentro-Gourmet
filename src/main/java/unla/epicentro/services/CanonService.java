package unla.epicentro.services;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import unla.epicentro.dtos.CanonDTO;
import unla.epicentro.entities.Costo;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.FoodTruck;
import unla.epicentro.entities.PuestoDesarmable;
import unla.epicentro.entities.UnidadDeVenta;

@Service
public class CanonService {

    public BigDecimal calcularCanon(UnidadDeVenta unidad, Festival festival) {

        if (unidad == null) {
            throw new IllegalArgumentException("La unidad de venta es obligatoria");
        }

        if (festival == null) {
            throw new IllegalArgumentException("El festival es obligatorio");
        }

        if (unidad.getSuperficie() == null) {
            throw new IllegalArgumentException("La unidad de venta no tiene superficie configurada");
        }

        if (festival.getCostos() == null) {
            throw new IllegalArgumentException("El festival no tiene costos configurados");
        }

        Costo costos = festival.getCostos();


        BigDecimal canonSuperficie = unidad.getSuperficie()
            .multiply(costos.getPorSuperficie());

        if (unidad instanceof FoodTruck foodTruck) {

            if (foodTruck.isConexionElectrica()) {
                return canonSuperficie.add(costos.getPorElectricidad());
            }

            return canonSuperficie;
        }

        if (unidad instanceof PuestoDesarmable puesto) {

            BigDecimal costoMontaje = costos.getPorMontaje()
                .multiply(BigDecimal.valueOf(puesto.getTiempoDeArmado()));

            return canonSuperficie.subtract(costoMontaje);
        }

        throw new IllegalArgumentException("Tipo de unidad de venta no soportado");
    }

    public CanonDTO obtenerCanon(UnidadDeVenta unidad,Festival festival) {

        BigDecimal canon = calcularCanon(unidad, festival);

        return new CanonDTO(unidad.getId(), unidad.getNombreComercial(), unidad.getCodigo(), unidad.getTipo(), canon);
    }

    public List<CanonDTO> obtenerCanonDeUnidades(Festival festival) {

        List<CanonDTO> resultado = new ArrayList<>();

        for (UnidadDeVenta unidad : festival.getUnidades()) {
            resultado.add(obtenerCanon(unidad, festival));
        }

        return resultado;
    }
}