
package unla.epicentro.services;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import unla.epicentro.entities.Costo;
import unla.epicentro.entities.Festival;
import unla.epicentro.entities.FoodTruck;
import unla.epicentro.entities.PuestoDesarmable;
import unla.epicentro.entities.UnidadDeVenta;

@Service
public class CanonService {

    public BigDecimal calcularCanon(UnidadDeVenta unidad, Festival festival) {

        if (unidad == null || festival == null) {
            throw new IllegalArgumentException(
                "La unidad y el festival son obligatorios"
            );
        }

        Costo costos = festival.getCostos();

        if (costos == null) {
            throw new IllegalArgumentException(
                "El festival no tiene costos configurados"
            );
        }

        BigDecimal canon = unidad.getSuperficie()
            .multiply(costos.getPorSuperficie());

        if (unidad instanceof FoodTruck foodTruck) {

            if (foodTruck.isConexionElectrica()) {
                canon = canon.add(costos.getPorElectricidad());
            }

        } else if (unidad instanceof PuestoDesarmable puesto) {

            BigDecimal costoMontaje = BigDecimal.valueOf(
                puesto.getTiempoDeArmado()
            ).multiply(costos.getPorMontaje());

            canon = canon.subtract(costoMontaje);

        } else {
            throw new IllegalArgumentException(
                "Tipo de unidad no reconocido"
            );
        }

        return canon;
    }
}
