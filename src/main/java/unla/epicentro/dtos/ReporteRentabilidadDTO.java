package unla.epicentro.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReporteRentabilidadDTO {

    private BigDecimal ingresos;
    private BigDecimal costosPlatos;
    private BigDecimal sueldos;
    private BigDecimal canon;
    private BigDecimal rentabilidadNeta;
}