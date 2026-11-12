package unla.epicentro.dtos;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReporteVentaDTO {

    private Long festivalId;
    private String nombreFestival;
    private BigDecimal recaudacionTotal;
}